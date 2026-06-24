package ex5.main;

import ex5.semantics.GeneralManager;
import ex5.semantics.SyntaxException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Entry point of the s-Java verifier: reads the source file, runs verification, and prints
 * 0, 1 or 2.
 *
 * @author Nurit Tolkowsky, Mili Green
 */
public class Sjavac {
	/**
	 * Prevents instantiation of this entry-point class.
	 */
	private Sjavac() {
	}

	private static final int LEGAL = 0;
	private static final int ILLEGAL = 1;
	private static final int IO_ERROR = 2;
	private static final int EXPECTED_ARG_COUNT = 1;
	private static final String SJAVA_SUFFIX = ".sjava";

	/**
	 * Runs the verifier on the single source file given as an argument and prints the result
	 * code (0 legal, 1 illegal, 2 IO error), with an informative message on stderr for 1 and 2.
	 *
	 * @param args the command-line arguments; expects a single .sjava file path
	 */
	public static void main(String[] args) {
		try {
			if (args.length != EXPECTED_ARG_COUNT) {
				throw new IOErrorException("Usage: Sjavac <file.sjava>");
			}
			String path = args[0];
			if (!path.endsWith(SJAVA_SUFFIX)) {
				throw new IOErrorException("Input file must end with .sjava");
			}

			List<String> lines;
			try {
				lines = Files.readAllLines(Path.of(path));
			} catch (IOException e) {
				throw new IOErrorException("Cannot read file: " + path);
			}

			new GeneralManager().verify(lines);
			System.out.println(LEGAL);
		} catch (SyntaxException e) {
			System.out.println(ILLEGAL);
			System.err.println(e.getMessage());
		} catch (IOErrorException e) {
			System.out.println(IO_ERROR);
			System.err.println(e.getMessage());
		}
	}
}
