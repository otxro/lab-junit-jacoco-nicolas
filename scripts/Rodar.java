import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;
import javax.tools.*;

/**
 * Compila o código (src/main/java) e os testes (src/test/java) e roda o JUnit 5
 * usando o único .jar em lib/. Não precisa de Maven nem de internet.
 *
 * Uso (na pasta do projeto):
 *   java scripts/Rodar.java
 *   java scripts/Rodar.java --select-class br.ufrgs.inf.lab.nivel1.ContaTest
 *   java scripts/Rodar.java --select-method br.ufrgs.inf.lab.nivel1.ContaTest#saqueValidoDeveDiminuirOSaldo
 */
public class Rodar {

    static final Path JAR = Paths.get("lib", "junit-platform-console-standalone-1.11.4.jar");
    static final Path MAIN_SRC = Paths.get("src", "main", "java");
    static final Path TEST_SRC = Paths.get("src", "test", "java");
    static final Path OUT = Paths.get("out");

    public static void main(String[] args) throws Exception {
        if (!Files.exists(JAR)) {
            erro("Nao achei " + JAR + ".\nVeja lib/LEIAME.md para baixar o arquivo.");
        }
        JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        if (javac == null) {
            erro("Este Java nao tem o compilador (voce tem so o JRE).\nInstale um JDK 17 ou superior.");
        }

        apagar(OUT);
        Path outMain = Files.createDirectories(OUT.resolve("main"));
        Path outTest = Files.createDirectories(OUT.resolve("test"));
        String sep = System.getProperty("path.separator");

        System.out.println(">> Compilando o codigo...");
        if (!compilar(javac, MAIN_SRC, "", outMain)) {
            erro("Erro de compilacao em src/main. Veja as mensagens acima.");
        }
        System.out.println(">> Compilando os testes...");
        if (!compilar(javac, TEST_SRC, outMain + sep + JAR, outTest)) {
            erro("Erro de compilacao nos testes (src/test). Veja as mensagens acima:\n"
                    + "arquivo e numero da linha estao na primeira linha de cada erro.");
        }

        List<String> cmd = new ArrayList<>(List.of(
                Paths.get(System.getProperty("java.home"), "bin", "java").toString(),
                "-jar", JAR.toString(),
                "execute",
                "-cp", outMain + sep + outTest,
                "--disable-banner", "--disable-ansi-colors", "--details-theme=ascii", "--details=tree"));
        if (args.length == 0) {
            cmd.add("--scan-class-path");
        } else {
            cmd.addAll(Arrays.asList(args));
        }

        System.out.println(">> Rodando os testes...\n");
        Process p = new ProcessBuilder(cmd).inheritIO().start();
        System.exit(p.waitFor());
    }

    static boolean compilar(JavaCompiler javac, Path src, String classpath, Path destino) throws IOException {
        List<Path> fontes;
        try (Stream<Path> s = Files.walk(src)) {
            fontes = s.filter(f -> f.toString().endsWith(".java")).toList();
        }
        if (fontes.isEmpty()) {
            return true;
        }
        try (StandardJavaFileManager fm = javac.getStandardFileManager(null, null, null)) {
            List<String> opcoes = new ArrayList<>(List.of("-encoding", "UTF-8", "-d", destino.toString()));
            if (!classpath.isEmpty()) {
                opcoes.addAll(List.of("-cp", classpath));
            }
            return javac.getTask(null, fm, null, opcoes, null, fm.getJavaFileObjectsFromPaths(fontes)).call();
        }
    }

    static void apagar(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            return;
        }
        try (Stream<Path> s = Files.walk(dir)) {
            for (Path p : s.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(p);
            }
        }
    }

    static void erro(String msg) {
        System.err.println("\nERRO: " + msg);
        System.exit(1);
    }
}
