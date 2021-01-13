## installation

## Python

```
pip install JPype1
```

## Java
```
cd Exercises
touch MANIFEST.MF
```
## JAR File

copy all the paths of .jar file in RobotControl/KUKAJavaLib and RobotControl/missing_jars to MANIFEST.MF. Pay attention to those files: jax-api-2.2.3.jar, jax-api-2.3.0.jar, jaxb-impl-2.2.6, jaxb-impl.2.3.0.  Please only choose one file of each version and if one version do not work in your system, change it to the other version. Otherwise it may cause 'NoSuchFieldError reflection' because of version conflict.
if you are using eclipse, go File -> Export -> Java -> JAR file -> click Exercise and RobotControl folder -> click next -> Use manifest from workspace -> Manifest file:/Exercises/MANIFEST.MF -> finish

## import JAR File
jarpath = os.path.join(os.path.abspath('.'), 'your relative JAR file path')
jpype.startJVM(classpath=[jarpath])
TEST = jpype.JClass('PackageName.ClassName')
jpype.shutdownJVM()
