# Sistema de Gestión "Epicentro Gourmet" - Hito 2

## 1. Información del Grupo
* **Número de Grupo:** Grupo 04
* **Materia:** Orientación a Objetos 2 (OO2)
* **Año Lectivo:** 2026

### Integrantes y Usuarios de GitHub
| Apellido y Nombre | Usuario de GitHub |
| :--- | :--- |
| D'Onofrio, Emanuel Humberto | [@EmanuelDonofrio](https://github.com/EmanuelDonofrio) |
| [Altieri, Angel Valentin] | [@angelvalentinn](https://github.com/angelvalentinn) |
| [Aznar, Iara Yael] | [@IaraAznar](https://github.com/IaraAznar) |
| [Luengo, Agustin] | [@luengo04](https://github.com/luengo04) |

---

##  Pasos para Correr la Aplicación

### Requisitos Previos
* **Java SDK:** 17 o 21 instalado.
* **Manejador de dependencias:** Maven Wrapper (incluido en el repositorio como `./mvnw` o `mvnw.cmd`).
* **Motor de Base de Datos:** MySQL Server 8.0+ en ejecución local (puerto por defecto `3306`).

### Configuración de la Base de Datos
1. Iniciar el servicio local de MySQL.
2. La aplicación creará la base de datos automáticamente si no existe gracias al parámetro `createDatabaseIfNotExist=true`. Sin embargo, si se prefiere crearla manualmente, ejecutar en la consola de MySQL:
   ```sql
   CREATE DATABASE IF NOT EXISTS epicentro_db;
Ejecutar el script SQL de prueba del repositorio para precargar roles, usuarios, festivales temáticos, costos y pedidos de prueba

### Variables de Entorno
La aplicación implementa variables de entorno para evitar la exposición de credenciales sensibles en el archivo de configuración application.yml
### Integrantes y Usuarios de GitHub
| Variable | Descripción | Valor por Defecto/ Ejemplo |
| :--- | :--- | :--- |
| DB_URL| URL JDBC de conexión con MySQL | jdbc:mysql://localhost:3306/epicentro_db?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true |
| USERNAME| Usuario administrador de MySQL| root |
| PASSWORD | Contraseña del usuario MySQL| root |

### Configuración en IntelliJ IDEA:

  1. Abrir el menú: Run > Edit Configurations...
  2. Localizar la aplicación bajo Spring Boot ( o Application)
  3. En la sección Environment variables, definir:
     DB_URL=jdbc:mysql://localhost:3306/epicentro_db?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true;USERNAME=root;PASSWORD=root
  4. Guardar los cambios con Apply y ejecutar con Run.

 ## Diagrama de Clases
   ### Enlace al diagrama de Clases (UML): https://drive.google.com/file/d/1lEi_lUVjO6VCx92LMhX7qrKeU6neyjAR/view

## Casos de Uso por Integrante
### Integrante: Emanuel Humberto D'Onofrio (@EmanuelDonofrio)
1. Caso de uso : Página de inicio (ver lista festival, primera vista).
   
      -Actor: usuario sin loggear.
   
      -Descripción: Es lo primero que se ve al iniciar la aplicación, tenés una lista de los festivales activos y dos botones, uno para registro de unidad de venta, otro para iniciar sesión.
   
      -Link al Pull Request: https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/2
   
2. Caso de uso : Autenticación, control de acceso por roles (Login)
 
      -Actor: usuario sin loggear
   
      -Descripción: el usuario hace click en iniciar sesión y lo lleva a la pestaña donde debe escribir su usuario y contraseña para poder logearse
   
      -Validaciones: El usuario tiene que estar previamente registrado, la contraseña no se puede visualizar, en su lugar aperecen puntos cada vez que se escribe algo en ese apartado.
       dependiendo del rol del usuario, una vez logeado puede tener acceso a diferentes procedimientos que otros usuarios no pueden visualizar.
   
      -Link al Pull Request: https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/1
   
4. Caso de uso : Registro Unidad de Venta
      -Actor: usuario sin loggear

       -Descripción: En el inicio, hace click en registrar unidad de venta, el usuario puede registrarse como un responsable y también puede registrar una unidad de venta, sea foodTruck o puestoDesarmable.
       se le crea un usuario como rol responsable.
   
      -Validaciones: El dni del responsable no tiene que ser repetido al igual que el nombre de usuario. El código de la unidad de venta se genera de forma aleatoria. La contraseña se encripta. (AVISO: validaciones agregadas en las últimas actualizaciones del proyecto).
   
      -Link al Pull Request: https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/8
   
5. Caso de uso : Visualizar perfil Empleado
      -Actor: Empleado (ROLE_EMPLEADO)

      -Descripción: El empleado, una vez que inició sesión, tiene acceso a la sección de su perfil donde se visualizan sus datos, unidad asignada y festival.
       
      -Link al Pull Request: https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/9
6. Caso de uso : Cálculo de Rentabilidad Neta Total
   
      -Actor: Responsable(ROLE_RESPONSABLE).
   
      -Descripción: Permite al Responsable consultar el balance económico neto de la unidad de venta a su cargo en un festival determinado.
       El sistema calcula la ganancia total restando los ingresos brutos por pedidos (totalVentas) el costo de producción acumulado de los platos vendidos (costoTotalPlatos),
       la liquidación proporcional de sueldos de todo el personal asignado y el canon devengado por el puesto en el evento.
   
      -Link al Pull Request: https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/20
   
8. Caso de uso : Cálculo de Rentabilidad Neta Por Fechas
   
      -Actor: Responsable(ROLE_RESPONSABLE).
   
      -Descripción: permite al Responsable evaluar el rendimiento financiero de su unidad de venta dentro de un intervalo temporal específico (fechaDesde y fechaHasta).
        El sistema filtra los pedidos registrados dentro de dicho rango horario y computa el balance restando de las ventas del período los costos de insumos
        de los ítems comercializados, el canon del predio y los haberes devengados por el staff

      - Validaciones:  fechaHasta tiene que ser sí o sí superior (más reciente) que  fechaDesde.
   
      -Link al Pull Request: https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/20

10. Caso de uso : Plato Estrella
    
      -Actor: Responsable ( ROLE_RESPONSABLE)
    
      -Descripción: Es una consulta que procesa el historial de pedidos de la unidad de venta en un festival y devuelve un objeto PlatoDTO correspondiente al plato que acumuló la mayor cantidad total de unidades vendidas.
      Se muestra destacado en la carta comercial indicando nombre, precios y cantidad total de pedidos registrados.
    
      -Link al Pull Request: https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/20
    
12. Caso de uso : Margen de Ganancia Promedio Menú ( caso de uso propio)
    
      -Actor: Responsable ( ROLE_RESPONSABLE).  
    
      -Descripción: Métrica analítica que calcula la rentabilidad porcentual promedio de la carta comparando precioVenta frente a precioCosto mediante la API Java Streams,
         resguardando contra división por cero y listas vacías.
    
      -Link al Pull Request: https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/23
    
13. Caso de uso : Reporte de Platos Críticos / Bajo Margen (caso de uso propio):
    
      -Actor: Responsable (ROLE_RESPONSABLE).
    
      -Descripción: Filtro analítico en memoria que identifica y resalta aquellos platos cuyo rendimiento unitario no alcanza el umbral (30%),
        integrando alertas y badges visuales diferenciados en platos.html
      -validaciones: el margen de ganancia del producto tiene que ser menor al 30% para que se muestre el aviso.
    
      -Link al Pull Request: https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/23



### Integrante: Angel Valentin Altieri (@angelvalentinn)
 1. Caso de uso 1: Alta, baja  y visualización de empleados desde el perfil de responsable
 2. Caso de uso 2: Envio de mail al dar de alta un empleado
 3. Caso de uso 3: Filtro por dni y atributos del empleado
 4. Caso de uso 4: Calculo de canon. Método que devuelve el monto que tiene que pagar la unidad de venta
 5. Caso de uso 5: Reporte de recaudación. Retorna la recaudación total bruta de la unidad de venta
 6. Caso de uso 6: Alta y modificación de platos
 7. Caso de uso 6: Habilitación de empleados deshabilitados

- Link a los PR (entre los pr hay algunos fixes): https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pulls?q=is%3Apr+state%3Aclosed+author%3Aangelvalentinn

### Integrante: Iara Yael Aznar (@IaraAznar)

**Caso de uso 1: Crear unidad de venta y asociarla a un festival**

- **Actor:** Administrador.
- **Descripción:** Permite al administrador registrar una nueva unidad de venta (Food Truck o Puesto Desarmable) desde la gestión de un festival determinado. Se registran los datos de la unidad, su responsable y las credenciales de acceso de este último. Una vez completado el registro, la unidad queda asociada automáticamente al festival seleccionado.
- **Validaciones:** Se verifica que el festival exista, que el DNI del responsable y el nombre de usuario no estén registrados previamente y que los datos ingresados cumplan con las validaciones correspondientes. La contraseña se almacena encriptada.
- **Link al Pull Request:** https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/29

**Caso de uso 2: Asignar unidades de venta existentes a un festival**

- **Actor:** Administrador.
- **Descripción:** Permite seleccionar una o varias unidades de venta previamente registradas y asociarlas a un festival determinado, evitando tener que registrarlas nuevamente.
- **Validaciones:** Se verifica que el festival exista, que se hayan seleccionado unidades y que todas las unidades seleccionadas sean válidas. Se evita duplicar la asociación de una misma unidad al mismo festival. Una unidad puede participar en distintos festivales.
- **Link al Pull Request:** https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/28

**Caso de uso 3: Visualizar unidades de venta por festival**

- **Actor:** Administrador.
- **Descripción:** Permite consultar las unidades de venta asociadas a un festival específico, visualizando sus datos principales, como identificador, nombre comercial y responsable asignado.
- **Validaciones:** Se verifica que el festival solicitado exista antes de mostrar sus unidades.
- **Link al Pull Request:** https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/4 y https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/14

**Caso de uso 4: Consultar ranking de unidades de venta por festival**

- **Actor:** Administrador.
- **Descripción:** Permite consultar un ranking de las unidades de venta pertenecientes a un festival, ordenadas según su recaudación, para comparar su rendimiento económico.
- **Validaciones:** El ranking se calcula considerando las unidades asociadas al festival seleccionado y se ordena de mayor a menor recaudación.
- **Link al Pull Request:** https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/15

**Caso de uso 5: Consultar unidades de venta con mayor canon por festival**

- **Actor:** Administrador.
- **Descripción:** Permite obtener un reporte de las unidades de venta que deben abonar los mayores montos de canon dentro de un festival determinado. El administrador puede indicar cuántas unidades desea visualizar.
- **Validaciones:** Se verifica que el festival exista. La cantidad de unidades a mostrar se establece mediante un parámetro, con un valor predeterminado de 3.
- **Link al Pull Request:** https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/25

### Integrante: Agustin Luengo (@luengo04)

 1. Caso de uso 1: Visualizar información como empleado
    
      -Actor: Empleado (ROLE_EMPLEADO)
    
      -Descripción: Inicia sesión y accede a la sección "Mi información" para consultar sus datos.
    
      -Validaciones: El usuario debe estar autenticado y tener un empleado asociado.
    
      -Link al Pull Request: https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/13
    
 2. Caso de uso 2: Consultar trabajo actual e historial
    
      -Actor: Empleado (ROLE_EMPLEADO)
    
      -Descripción: Consulta la unidad de venta y festival asociadas al empleado.
    
      -Validaciones: El empleado debe tener una unidad de venta asignada.
    
      -Link al Pull Request: https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/13
    
 3. Caso de uso 3: Consultar liquidación de sueldo y recibo en PDF
    
      -Actor: Empleado (ROLE_EMPLEADO)
    
      -Descripción: El empleado consulta la liquidación salarial conforme a la consigna y puede descargarlo en formato PDF.
    
      -Link al Pull Request: https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/13
    
 4. Caso de uso 4: Crear pedidos
    
      -Actor: Cajero (ROLE_EMPLEADO)
    
      -Descripción: El cajero selecciona platos del menú con su cantidad y confirma el pedido.
    
      -Validaciones: Sólo el cajero puede acceder.
    
      -Link al Pull Request: https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/24
    
 5. Caso de uso 5: Apertura y cierre de caja
     
      -Actor: Cajero (ROLE_EMPLEADO)
    
      -Descripción: El cajero puede abrir la caja de su unidad de venta para registrar ventas y cerrarla al finalizar la jornada.
    
      -Validaciones: Chequeo del estado de la caja.
    
      -Link al Pull Request: https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/26
    
 6. Caso de uso 6: Consultar historial de pedidos

      -Actor: Cajero (ROLE_EMPLEADO)
     
      -Descripción: El cajero puede consultar los pedidos realizados en su unidad de venta.
     
      -Link al Pull Request: https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/27
     
 7. Caso de uso 7: Registrar observaciones en pedidos
    
      -Actor: Cajero (ROLE_EMPLEADO)
    
      -Descripción: Al crear un pedido, se puede realizar indicaciones extra.
    
      -Link al Pull Request: https://github.com/EmanuelDonofrio/TPGrupalOO2Hito2Grupo04/pull/27


    
    

   
