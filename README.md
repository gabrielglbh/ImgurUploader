# ImgurUploader

Una aplicación que utiliza la API de Imgur para poder administrar las fotos de tu perfil de imgur:

- Autenticación mediante OAuth con Imgur
- Recogida de las fotos de imgur del perfil
- Subida de fotos tanto desde la galería como de la cámara del dispositivo
- Eliminación de fotos de tu perfil

## Arquitectura y Dependencias

Para desarrollar esta aplicación, he optado por una arquitectura limpia y de desacoplamiento: DDD (Domain Driven Development) y MVVM. Esta arquitectura, aunque enrevesada, simplifica la manera de comunicarse con los servicios externos como la API de Imgur, manteniendo la arquitectura arquetipo de Android.

Esta arquitectura se basa en contratos y abstrae por capas el código:

- La capa de __Dominio__ contiene los contratos por los que se tienen que regir los servicios de terceros. Son interfaces de métodos a cumplir. Esta capa contiene además los objetos traducidos de los servicios utilizados por la capa de __Presentación__.

- La capa de __Infraestructura__ implementa los contratos e implementa los métodos directamente del servicio para obtener datos o actualizarlos. Además, al igual que en la capa de Dominio, contiene los objetos literales extraídos de los datos, en el caso de la API de Imgur, el raw JSON. Esta capa se encarga de traducir estos objetos raw al dominio de la aplicación.

- La capa de __Aplicación__ es la que se comunica con los contratos y la capa de Presentación.  Siguiendo el patrón implementado, esta funcionalidad recaería en los ViewModel. Aquí se llaman a los métodos abstraídos del contrato, que a su vez llaman (mediante inyección de dependencias) a los métodos implementados de la capa de __Infraestructura__ para obtener los datos.

- La capa de __Presentación__ es donde se encuentra toda la interfaz de usuario. Para recoger y actualizar datos, las actividades o fragmentos solo tienen que pedírselos al ViewModel designado, siguiendo el patrón MVVM, quedando la UI totalmente abstraída de los servicios de terceros.

De esta manera, si se quisiera cambiar de servicio de autenticación o de proveedor de imágenes a Pinterest, solo haría falta cambiar la capa de Infraestructura, ya que el contrato seguiría vigente para el resto de la aplicación.

Para hacer posible esta arquitectura y la aplicación en sí he utilizado:

- __Hilt__ como inyector de dependencias, ya que es la recomendada por Google.

- __Corutinas__ para las llamadas asíncronas.

- __StateFlow__ para la comunicación entre ViewModel y Vista.

- __Retrofit__ para las llamadas a la API de Imgur por ser el más estandarizado cliente para las comunicaciones con API y por su fácil implementación.

- __Coil__ para la muestra de las imágenes. Era este, Glide o Picasso, pero me he decantado por Coil, ambos son lo mismo.

- __Arrow__ para la devolución única de un Type en las llamadas a los servicios. Esto ayuda a simplificar el código ya que sen un solo tipo Either te incluye el error o el dato.

- __Jetpack Compose__ para toda la interfaz. Soy capaz de hacer las apps con las vistas XML , bindings y ConstraintLayouts… pero me parecía más rápido y directo con Compose (véase Dificultades y Retos #8).

## Organización del Código

Siguiendo las pautas de la arquitectura, se han creado 4 carpetas raíz: di, domain, infraestructure y presentation.

- En __di__ se encuentran los módulos de Hilt que declaran la inyección de dependencias.

- __Domain__ se subdivide por carpetas relacionadas con los servicios. Por cada servicio estará el contrato y, en caso necesario, el objeto de respuesta adaptado para la app.

- __Infrastrucure__ también se subdivide por carpetas relacionadas con los servicios. Por cada una, estará la implementación del contrato y, en caso necesario, el objeto raw del servicio.

- __Presentation__ se encuentran divididas por carpetas todas las actividades y fragmentos de la app, que a su vez incluyen su propio ViewModel. Además está la carpeta shared, que contienen los componentes reutilizables como validadores de campos o constantes.

- En __res__ se encuentran todos los layouts de fragmentos, actividades y componentes tanto en portrait como en landscape, además de los drawables (iconos), strings y estilos de la app. 

## Dificultades y Retos

1. La primera dificultad ha sido con los __tests unitarios__. Vengo de desarrollar test unitarios y de integración con Flutter y conozco mockito y todos sus usos, pero me costó adaptarme a la sintaxis de Kotlin. Una vez le pillé el truco a cómo funcionaban, todo se hizo mas llevadero. Además, la programación de los tests unitarios de las implementaciones de los contratos han sacado a la luz más de una dependencia que no había abstraído bien como la de Retrofit por ejemplo.

2. __Test de integración y de UI__: he intentado hacer pruebas de UI con espresso, pero he de reconocer que este ámbito de la programación de apps de Android no es mi punto fuerte. He dejado un resquicio de mi intento. Me da mucha rabia porque sé hacer tests de integración en Flutter, pero aquí es un poco distinto. ¡Estoy deseando aprender cómo hacer mejor las pruebas!

3. __La toma de fotos tanto de la cámara como de la galería__ ha sido otro quebradero de cabeza, sobre todo por los permisos a pedir, por el manejo de los intents y por las APIs de ActivityResultLauncher. Se ha conseguido hacer, pero estuve una tarde entera con la validación de la toma de imágenes de la cámara. Lo que más me llevó descubrir fue el tag de _provider_ que se debe incluir en el manifiesto para que no infiera el intent con otros proveedores de fotos.

4. Mientras trabajaba en la __implementación de la API de Imgur con Retrofit__, me topé con que las anotaciones de GET, POST… debían de ponerse en la capa de Dominio, al declarar ahí la interfaz o contrato de lo que quería: coger imágenes, coger el nombre de usuario… Esto rompía un poco la arquitectura de la aplicación, pero Retrofit, al ser un componente vital, lo dejé pasar. Si se quiere migrar a OkHttp, habría que cambiar el contrato y a su vez, correctamente, la implementación del contrato de HttpRepositoryImpl. Más tarde, encontré una solución para esto: declarar dos veces el mismo contrato pero con distinto objetivo. Para mantener el sentido de la arquitectura, implementé que el builder de Retrofit me devolviera la clase ImageManagerCalls. Es en esta clase donde se implementan las anotaciones de Retrofit y este archivo está localizado en la capa de Infraestructura. A su vez, para replicar las llamadas de manera correcta desde la capa de presentación, implementé ImageManagerRepository en la capa de dominio, con los mismos métodos pero sin ninguna declaración ajena al código de la app. De esta manera, En la implementación del repositorio de ImageManager, se hacen llamadas al servicio creado por Retrofit de ImageManagerCalls, abstrayendo completamente las capas.

5. Durante la implementación de los tests unitarios, llegué a un punto en el que tuve que mockear métodos estáticos como Uri.parse o Call.await de Retrofit. Hasta un punto, utilicé mockito pero esta librería no admite el mockeado de clases ni métodos estáticos. Por eso, __tuve que migrar de mockito a mockK__. Este cambio, aunque relativamente fácil, tuvo sus momentos complicados, como por ejemplo con las Task.await de las corrutinas, que se deben probar de manera distinta: sin probar el await, directamente mockeando los métodos de isSuccessful, isCompleted…

6. Sin duda lo más complejo ha sido __manejar y entender la lógica de la API de Imgur__. Pese a que está bien documentada con Postman, el método de upload no indica cómo has de construir tu request específica. En el caso de retrofit, estaba pasando la imagen, el titulo y la descripción como un body normal, ya que pasaba la imagen como base64. Después y tras mucho prueba y error, logré entender que había que mandar los datos como multipart. No había oído nunca esto, y por eso fue bastante difícil de asimilar y programar. Cambié la imagen para que se mandará como File y construí el objeto RequestBody para mandarlo. Efectivamente, la API ya estaba respondiendo errores normales, pero me apareció el fatídico error 417. Tras mucho diseccionar el código y la documentación de Imgur, llegué a la conclusión de que las imágenes eran demasiados grandes como para subirlas, así que manteniendo el ratio de imagen, escalé las imágenes para que tuvieran una anchura de 400 pixeles y la API comenzó a responder con 200 y a subir fotos correctamente.

7. Como se verá en el registro de git, __entendí el login como algo estándar y no para autenticarse con Imgur y poder realizar llamadas a sus endpoints__. Por eso, utilicé Firebase Auth con un login sencillo. Tras muchas horas de debuguear el código debido a los fallos con los demás endpoints de imgur, llegué a la conclusión que, claramente, no estaba logueado como ningún usuario y que por eso me fallaban. Indagando y leyendo bien la documentación de la API, pude implementar la autorización de Imgur mediante la uri de /oauth2/authorize y con un intent-filter para el callback de la autorización para recoger debidamente los tokens y almacenarlos en Shared Preferences. Después de eso, y de arreglar vario bugs relacionados con GSON, por fin pude recoger al usuario con la sesión iniciada previa.

8. Tras tener la app prácticamente lista con los flujos básicos requeridos, __migré de Jetpack Compost a Vistas con XML__. ¿Porqué? Jetpack Compose es muy parecido a Flutter en cuanto a la programación de la interfaz, pero en el trabajo al que estoy aplicando, como se me dijo, se trabaja con Vistas y no con Compose. Por esto, decidí migrar a vistas, para que se aprecie que no solo sé hacer Compose. Aun así, en el MR #3, se pueden ver todos los cambios para esta migración. Otro punto para esta migración es el tratamiento de la orientación del dispositivo: con Vistas la separación de los archivos landscape y portrait hace la programación de interfaz mucho más limpia que con Compose (lleno de condicionales).
