package ex5.main;

import ex5.semantics.GeneralManager;
import ex5.semantics.SyntaxException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Sjavac {
    public static void main(String[] args){
        try{
            if (args.length != 1){
                throw new IOErrorException("Usage: Sjavac <file.sjava>");
            }
            String path = args[0];
            if (!path.endsWith(".sjava")){
                throw new IOErrorException("Input file must end with .sjava");
            }

            List<String> lines;
            try {
                lines = Files.readAllLines(Path.of(path));
            } catch (IOException e) {
                throw new IOErrorException("Cannot read file: " + path);
            }

            new GeneralManager().verify(lines);
            System.out.println(0);
        } catch (SyntaxException e) {
            System.out.println(1);
            System.err.println(e.getMessage());
        } catch (IOErrorException e) {
            System.out.println(2);
            System.err.println(e.getMessage());
        }
    }
    }