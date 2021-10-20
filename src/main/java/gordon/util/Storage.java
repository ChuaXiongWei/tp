package gordon.util;

import gordon.exception.GordonException;
import gordon.kitchen.Cookbook;
import gordon.kitchen.Recipe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Storage {
    public static final String PATHNAME = Paths.get("saveFile.txt").toString();
    static Logger logger;

    public Storage(Cookbook cookbook) {
        logger = Logger.getLogger(GordonException.loggerName);
        File load = new File(PATHNAME);

        try {
            Scanner loadScanner = new Scanner(load);

            while (loadScanner.hasNext()) {
                Recipe r = new Recipe(loadScanner.nextLine());
                loadCalories(r, loadScanner);
                loadIngredients(r, loadScanner);
                loadSteps(r, loadScanner);
                loadTags(r, loadScanner, cookbook);
                cookbook.addRecipe(r);
            }

            logger.log(Level.INFO, "Previous session restored.");

        } catch (GordonException e) {
            System.out.println("GordonException:" + e.getMessage());
        } catch (FileNotFoundException | ArrayIndexOutOfBoundsException e) {
            try {
                load.createNewFile();
                logger.log(Level.INFO, "Save file not found. Creating new file.");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void loadCalories(Recipe r, Scanner loadScanner) {
        String line = loadScanner.nextLine().trim();
        assert (line.contains("Calories"));
        String[] lineSplit = line.split(":");

        // Regex to extract integers from String
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(lineSplit[1]);
        while (m.find()) {
            r.setCalories(Integer.parseInt(m.group()));
        }
    }

    public void loadIngredients(Recipe r, Scanner loadScanner) {
        String line = loadScanner.nextLine().trim();
        if (line.equals("Ingredients needed:")) {
            while (loadScanner.hasNext()) {
                line = loadScanner.nextLine().trim();
                int dotIndex = line.indexOf('.');

                if (dotIndex < 0) {
                    break;
                }

                String parsedIngredient = line.substring(dotIndex + 2);
                r.addIngredient(parsedIngredient);
            }
        }
    }

    public void loadSteps(Recipe r, Scanner loadScanner) {
        while (loadScanner.hasNext()) {
            String line = loadScanner.nextLine().trim();
            int dotIndex = line.indexOf('.');

            if (dotIndex < 0) {
                break;
            }

            String parsedStep = line.substring(dotIndex + 2);
            r.addStep(parsedStep);
        }
    }

    public void loadTags(Recipe r, Scanner loadScanner, Cookbook cookbook) {
        while (loadScanner.hasNext()) {
            String line = loadScanner.nextLine().trim();
            int dotIndex = line.indexOf('.');

            if (dotIndex < 0) {
                break;
            }

            String parsedStep = line.substring(dotIndex + 2);
            Tag createdTag = new Tag(parsedStep, r.getName());

            if (!cookbook.doesCookbookTagExists(parsedStep)) {
                cookbook.addCookbookTag(createdTag);
            } else {
                cookbook.appendRecipeToCookbookTag(createdTag.getTagName(), r.getName());
            }
            r.addTagToRecipe(createdTag, r.getName());
        }

    }

    public void saveCookbook(Cookbook cookbook) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < cookbook.numRecipes(); i++) {
            output.append(cookbook.saveString(i));
            output.append(System.lineSeparator());
        }
        try {
            FileWriter writer = new FileWriter(PATHNAME);
            writer.write(output.toString());
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}