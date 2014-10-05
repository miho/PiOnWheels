PiOnWheels
==========

![](/resources/img/pow-01.png)

![](/resources/img/pow-02.png)

Make sure to clone the slides as well: https://github.com/miho/JavaOne-2014

## How to Build PoW

### Requirements

- Java >= 1.8
- Internet connection (dependencies are downloaded automatically)
- IDE: [Gradle](http://www.gradle.org/) Plugin (not necessary for command line usage)

### IDE

Open the `PoW` [Gradle](http://www.gradle.org/) project in your favourite IDE (tested with NetBeans 8.0) and build it
by calling the `assemble` task.

### Command Line

Navigate to the [Gradle](http://www.gradle.org/) project (e.g., `path/to/PoW`) and enter the following command

#### Bash (Linux/OS X/Cygwin/other Unix-like shell)

    sh gradlew assemble
    
#### Windows (CMD)

    gradlew assemble
