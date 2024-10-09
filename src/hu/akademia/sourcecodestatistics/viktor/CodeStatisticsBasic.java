package hu.akademia.sourcecodestatistics.viktor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.stream.Stream;

class CodeStatisticsBasic {
	public static void main(String[] args) {
		try {
			new CodeStatisticsBasic().run(args);
		}catch(InvalidPathException ipe) {
				System.out.println(ipe.getMessage());
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		} catch (IllegalArgumentException iae) {
			System.out.println(iae.getMessage());
		} 
	}

	private void run(String[] args) throws IOException, IllegalArgumentException {
		if (args.length < 1) {
			throw new IllegalArgumentException("Please provide the filename as a command-line argument!");
		}
		try {
			Path path = createAbsolutePath(args);
			printIndicators(path);
		} catch (IOException ioe) {
			throw new IOException("The given path cannot be traversed or the files on this path cannot be open!");
		}
	}

	private Path createAbsolutePath(String[] args) {
		Path path = Path.of(args[0]);
		return path.toAbsolutePath();
	}

	private void printIndicators(Path path) throws IOException {
		System.out.printf("In the %s directory there are %,d project(s). The number of source files are %,d. The overall number of the sourcefile lines are %,d.", //
				path.toString(), countProjects(path), countFiles(path), countLines(path));
	}

	private long countProjects(Path path) throws IOException {
		try (Stream<Path> stream = Files.walk(path, 1)) {
			return stream.count() - 1;
		}
	}

	private long countFiles(Path path) throws IOException {
		try (Stream<Path> stream = Files.walk(path)) {
			return stream.filter(Files::isRegularFile)//
					.filter(item -> item.toString().endsWith(".java"))//
					.count();
		}
	}

	private long countLines(Path path) throws IOException {
		try (Stream<Path> stream = Files.walk(path)) {
			return stream.filter(Files::isRegularFile)//
					.filter(item -> item.toString().endsWith(".java"))//
					.mapToLong(file -> countLines0(file))//
					.sum();
		} catch (UncheckedIOException ue) {
			throw ue.getCause();
		}
	}

	private long countLines0(Path path) throws UncheckedIOException {
		try (Stream<String> lines = Files.lines(path)){
			return lines.count();
		} catch (IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}
}
