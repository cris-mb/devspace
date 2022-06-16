# Idea

# 1. Tipo de proyecto
Le quiero dar un enfoque de desarrollo de un entregable al proyecto, y así continuar con el prototipo en el que trabajé durante el ciclo en PMDM (Programación Multimedia e Dispositivos Móbiles).

# 2. Propósito principal
Mi proyecto se basa en una red social escrita en Java (Android), cuyo principal objetivo es proponer una nueva forma de estudio a cualquier persona interesada en aprender sobre lenguajes de programación.

Además fomenta el uso de código abierto y sirve como escaparate o forma de promoción para aquellos que publican sus conocimientos sobre el tema.

# 3. Perfil del cliente
Esta aplicación está dirigida a aquellas personas que trabajen en el mundo de desarrollo de software, que tengan la necesidad de aprender un nuevo lenguaje o una nueva tecnología y quieran prescindir de los métodos más convencionales de aprendizaje. Definiendo mejor el perfil de los usuarios, en su gran mayoría, serían jóvenes de entre 20 y 40 años que empiezan su carrera en el sector y usan habitualmente redes sociales en su día a día.

# 4. Necesidades a cubrir
Las necesidades a cubrir son más evidentes teniendo en cuenta el perfil anteriormente descrito:

   ### 1. La necesidad de disponer de una una forma de aprendizaje más intuitiva, llamativa, dinámica y clara, en comparación con las convencionales APIs y javadocs, por ejemplo.   
   Como estudiante de desarrollo de software, noté que la documentación por la que solía estudiar a veces era pesada, mal estructurada, poco clara y sobre todo, poco llamativa y atractiva para el lector. De ahí surgió la idea de combinar una red social con imágenes, vídeos y comentarios, con los que acompañar a la documentación del código. Así el proceso de aprender una nueva tecnología sería más ameno y atractivo.

   ### 2. La necesidad de disponer de un método de constante actualización sobre las nuevas tecnologías incipientes
   Otros de los aspectos que cubre esta aplicación es la creación de una nueva vía para informarse sobre las nuevas tecnologías que aparecen en el sector o sobre los cambios importantes de las ya existentes. Esta necesidad puede ser que la cubra alguna red social ya existente, como por ejemplo Twitter lo hace con su gran comunidad sobre hacking ético y ciberseguridad, con la que mantenerse al tanto de lo que sucede en el sector.

# 5. Competencia
Por otra parte, al pensar en posibles competidores que presenten un producto similar a DevSpace, el primer ejemplo claro sería GitHub con su apartado _Explore_, donde los usuarios de esta plataforma suben publicaciones sobre eventos, nuevas tecnologías, etc. Para suplir esta comparación, decidí implementar un apartado de _Mensajes_, para que los usuarios dispongan de una forma de comunicarse cómoda, rápida y eficaz.

# 6. Objetivos y requisitos
Los objetivos principales de la aplicación que se pretenden llevar a cabo son:

  * Que el usuario pueda ingresar en la aplicación de una forma intuitiva y rápida.
  * Configurar algunos aspectos de su cuenta en el apartado de preferencias.
  * Crear publicaciones con las que compartir código con la comunidad.
  * Interactuar con las publicaciones de otros usuarios a través de likes y comentarios.
  * Comunicarse con otros usuarios mediante el sistema de mensajería instantánea. 

Las funcionalidades que implementa para cumplir con estos objetivos son :

  * **Sistema de mensajería instantánea**. Para facilitar la comunicación entre usuarios que tengan alguna duda, quieran contribuír a un proyecto, ...

  * **Publicaciones**. Estas son la principal funcionalidad de la aplicación. Mediante estas el usuario puede publicar imágenes, vídeos e incluso documentación externa a DevSpace de su código.

  * **Buscador**. Mediante el buscador podrás filtrar las publicaciones que desees encontrar.

  * **Preferencias**. En este apartado el usuario puede cambiar algunos aspectos de su cuenta y de la aplicación.

  * **Sistema de login**. La aplicación cuenta con un sistema de login rápido e intuitivo, con el que incluso se puede registrar rápidamente con su cuenta de Google

# 7. Tecnologías usadas
Las tecnologías usadas son Java (Android) para la creación de la aplicación en sí, y para la parte de la base de datos escogí Firebase. Esta tecnología no fue vista en el ciclo y aporta la infraestructura necesaria para presentar un prototipo de aplicación en condiciones. Usando los servicios de Firebase, pude conectar mi aplicación a una base de datos remota con la que autenticar a mis usuarios, guardar sus publicaciones, imágenes, vídeos, etc. Al ser una tecnología no vista en el ciclo y tener que pasar de una base de datos relacional a una documental, el migrar la antigua base de datos en local con la que contaba la aplicación fue un paso grande y tedioso

Para más información al respecto, consulta el [Anteproyecto](/doc/documentation/anteproyecto.pdf)

