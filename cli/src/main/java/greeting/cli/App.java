package greeting.cli;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import greeting.api.GreetingService;

/**
 * A remarkably over-engineered "Hello, world!" application.
 */
public class App {
  private static Set<Provider<GreetingService>> extensionProviders = new HashSet<>();

  public static void main(String[] args) throws IOException {
    do {
      // Scan the paths given as arguments for extensions.
      loadExtensions(args);

      // Use each retrieved extension to print a greeting.
      extensionProviders.forEach(
          provider -> System.out.printf(
              "[%s] %s\n",
              provider.type().getSimpleName(),
              provider.get().createMessage()));

      // Request to run again so that the
      // user can alter the composition of
      // the extension paths at runtime.
      System.out.println("\nRun again? [Y/n] ");
    } while (System.in.read() != 'n');
  }

  /**
   * Traverses the given paths in search for extensions and tries to load them.
   *
   * @param paths The paths the check for extensions.
   * @throws IOException
   */
  private static void loadExtensions(String[] paths) throws IOException {
    // Clean up the extensions.
    extensionProviders.clear();

    // For each provided path...
    Set<URL> urls = new HashSet<>();
    for (String path : paths) {
      File extensionPath = new File(path);

      // Get a list of files with 'jar' extension.
      File[] jarsInPath = extensionPath.listFiles(
          file -> file.getPath().toLowerCase().endsWith(".jar"));

      // For each found JAR, get its URL.
      for (File jar : jarsInPath) {
        // Validate the JAR.
        if (isJarValid(jar)) {
          // The JAR is valid, collect its URL.
          urls.add(jar.toURI().toURL());
        }
      }
    }

    // Create a URL-based class loader with the collected URLs.
    URLClassLoader urlClassLoader = new URLClassLoader(urls.toArray(new URL[0]));

    // Load the service providers using the URL class loader.
    ServiceLoader<GreetingService> loader = ServiceLoader.load(
        GreetingService.class,
        urlClassLoader);

    // Add each provider found in the JAR.
    // Please note that, by using stream, we get
    // references to lazily load the service providers.
    loader.stream().forEach(extensionProviders::add);
  }

  /**
   * Validate each entry in the provided JAR file.
   *
   * @param jarFile The JAR file.
   * @return True if the JAR is signed and has not been tampered with.
   * @throws IOException If there was a problem reading from the JAR file.
   */
  private static boolean isJarValid(File jar) throws IOException {
    try (JarFile jarFile = new JarFile(jar)) {
      Enumeration<JarEntry> entries = jarFile.entries();
      while (entries.hasMoreElements()) {
        JarEntry entry = entries.nextElement();
        // Obtaining the input stream will throw a
        // SecurityException if the signature check fails.
        try (InputStream is = jarFile.getInputStream(entry)) {
          // Java 8 requires the input stream to be read too,
          // not only obtained.
          byte[] buffer = new byte[8192];
          while ((is.read(buffer, 0, buffer.length)) != -1)
            ;
          // Look for certificates used to sign.
          if (entry.getCodeSigners() != null) {
            // For this demo, we're satisifed that there is a certificate.
            // In a production application, each relevant certificate would
            // be validated here.
            return true;
          }
        } catch (SecurityException se) {
          // A security exception means that one of
          // the signatures could not be validated.
          return false;
        }
      }
    }

    // No certificates were found though no exception was thrown,
    // so the JAR must not be signed.
    return false;
  }
}
