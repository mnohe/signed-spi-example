# Signed service provider demo

This repository showcases some Java technologies that allow extending an application securely. This technical demo uses Java's service provider interface and JAR signing to allow a client application to dynamically load and validate external libraries.

The [cli/](./cli/) folder holds a console application that accepts any number of paths as arguments, and will scan these locations for JARs with classes that match the service provider interface. The ones that are signed (and un-tampered) will be used to output a greeting message. The [formal/](./formal/) folder provides such a library.

> [!WARNING]
> Please note that this is a technical demonstration, and is **not intended for production** use as it is. In particular, JARs should be signed with certificates issued by a trusted authority, and validated accordingly.

## Running

1. Package the `cli/` project to generate the client application:
    ```console
    mvn package -f formal/
    ```
1. Run the client application:
    ```console
    java -jar cli/target/cli-1.0-SNAPSHOT.jar
    ```
    The output should show only the built-in greeting service:
    ```
    [HelloService] Hello, world!

    Run again? [Y/n]
    ```
    Answer `y` or just press Enter, and you will get the same message.
1. In another terminal, execute the `package` phase in the Maven project in the `formal` folder to create and sign the extension:
    ```console
    mvn package -f formal/
    ```
1. Copy the signed extension to the `extensions/` folder of the client application folder:
    ```console
    cp formal/target/formal-1.0-SNAPSHOT.jar cli/extensions/
    ```
1. Back to the initial terminal, answer `y` once more, and you should see the new greeting provider's message:
    ```
    [HelloService] Hello, world!
    [FormalGreetingService] How do you do?

    Run again? [Y/n]
    ```

You have just run a client application that can be extended at runtime with vetted extensions.

> [!NOTE]
> There is a [dev container] with Java 21 and Maven, which should allow running this project straight away in any Docker-equipped environment.

## Keystore

For convenience, the extension folder holds [a keystore used to sign the JAR](./formal/abc.keystore.jks):
```console
keytool -genkeypair \
  -alias ABC \
  -keyalg RSA \
  -keystore abc.keystore.jks
```

## Further reading

- [ServiceLoader API](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/ServiceLoader.html)
- [Java security overview](https://docs.oracle.com/en/java/javase/21/security/java-security-overview1.html)
- [`jarsigner` documentation](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jarsigner.html)
- [SecurityManager and Policy overview](https://docs.oracle.com/en/java/javase/21/security/java-security-overview1.html#GUID-2779612B-8DE1-49D3-8EC3-C678C3A9FC35)
- [Signing code and granting permissions](https://docs.oracle.com/javase/tutorial/security/toolsign/index.html)
