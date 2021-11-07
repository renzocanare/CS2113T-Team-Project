package seedu.foodorama;

import seedu.foodorama.exceptions.FoodoramaException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class that handles the collection of ingredient objects at runtime
 * @author renzocanare, jhsee5, Rakesh12000, Dniv-ra
 */
public class IngredientList {
    public static final String YES_NO_REGEX = "^(y|yes|n|no)$";
    private static final Ui UI = new Ui();
    private static final String YES = "y";
    private static final String NO = "n";
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final int TEN_YEARS_IN_DAYS = 3650;
    private static final int ZERO_DAYS = 0;
    public static ArrayList<Ingredient> ingredientList = new ArrayList<>();

    /**
     * Adds a new ingredient to the ingredient list
     * @param ingredientName name of the ingredient to be added
     * @throws FoodoramaException if the weight of the ingredient is invalid
     *
     * @author jhsee5
     */
    public static void add(String ingredientName) throws FoodoramaException {
        UI.printEnterWeightOf(ingredientName);
        Scanner in = new Scanner(System.in);
        String ingredientWeight = in.nextLine();

        int exitloop = 0;
        double ingredientWeightValue;
        while (exitloop == 0) {
            String confirmAdd = "e";
            if (!isNumber(ingredientWeight)) {
                throw new FoodoramaException(UI.getInvalidNumberMsg());
            }

            ingredientWeightValue = Double.parseDouble(ingredientWeight);
            while (ingredientWeightValue < 0) {
                UI.clearTerminalAndPrintNewPage();
                UI.printInvalidIngrWeight(ingredientName);
                ingredientWeight = in.nextLine();
                ingredientWeightValue = Double.parseDouble(ingredientWeight);
            }
            if (Double.isInfinite(ingredientWeightValue) | Double.isNaN(ingredientWeightValue)) {
                throw new FoodoramaException(UI.printNumericalInputInvalid("dish waste"));
            } else if (ingredientWeightValue > 10000) {
                UI.clearTerminalAndPrintNewPage();
                UI.printIngrValueHigh(ingredientName);
                confirmAdd = in.nextLine();

                confirmAdd = getConfirmation(confirmAdd);
                if (confirmAdd.startsWith(NO)) {
                    UI.clearTerminalAndPrintNewPage();
                    UI.printEnterWeightOf(ingredientName);
                    ingredientWeight = in.nextLine();
                    ingredientWeightValue = Double.parseDouble(ingredientWeight);
                }
            }
            if ((isNumber(ingredientWeight) && (ingredientWeightValue >= 0)
                    && (ingredientWeightValue <= 10000)) | confirmAdd.startsWith(YES)) {
                exitloop = 1;
            }
        }

        ingredientWeightValue = Double.parseDouble(ingredientWeight);
        Ingredient ingredientToAdd = new Ingredient(ingredientName, ingredientWeightValue);
        ingredientList.add(ingredientToAdd);
        UI.printAddedIngredient(ingredientToAdd, ingredientWeightValue);

    }

    /**
     * Checks if an ingredient exists in the ingredient list and returns it index
     * @param ingredientName name of ingredient being searched for
     * @return -1 if not present, index if present
     *
     * @author renzocanare
     */
    public static int find(String ingredientName) {
        for (Ingredient ingredient : ingredientList) {
            if (ingredient.getIngredientName().equals(ingredientName)) {
                return ingredientList.indexOf(ingredient);
            }
        }
        return -1;
    }

    /**
     * Gets the value of the ingredient with the largest waste
     * @return largest wastage present in the list
     *
     * @author Dniv-ra
     */
    public static double getGreatestWaste() {
        double greatest = 0;
        for (Ingredient ingr : ingredientList) {
            double currWaste = ingr.getWastage();
            if (currWaste > greatest) {
                greatest = currWaste;
            }
        }
        assert greatest != 0 : "Exception should have been thrown earlier if list is empty";
        return greatest;
    }

    /**
     * Calls the graph function for ingredients
     *
     * @author Dniv-ra
     */
    public static void graph() {
        UI.printIngrListGraph(ingredientList);
    }

    /**
     * Calls the list function for ingredients
     *
     * @author renzocanare
     */
    public static void list() {
        UI.printIngrList(ingredientList);
    }

    /**
     * Deletes an ingredient from the ingredient list
     * @param ingredientIndex index of the item to be deleted
     *
     * @author Rakesh12000
     */
    public static void delete(int ingredientIndex) {
        Scanner input = new Scanner(System.in);
        int listSize = ingredientList.size(); //listSize = N
        String ingredientName = ingredientList.get(ingredientIndex).getIngredientName();
        if (ingredientIndex == -1) {
            UI.printIngrNotExistMsg();
            assert ingredientList.size() == listSize : "ingredientList should be of size N";
        } else {
            UI.printConfirmDelIngr(ingredientName);
            String confirmDel = input.nextLine().toLowerCase();
            while (!confirmDel.matches(YES_NO_REGEX)) {
                UI.clearTerminalAndPrintNewPage();
                UI.printInvalidConfirmation();
                confirmDel = input.nextLine().toLowerCase();
            }
            UI.clearTerminalAndPrintNewPage();
            if (confirmDel.startsWith(YES)) {
                //Get all dishes
                for (Dish dish : DishList.dishList) {
                    //Find if they contain ingr in parts
                    ArrayList<Ingredient> parts = dish.getParts();
                    for (int i = 0; i < parts.size(); i++) {
                        if (parts.get(i).getIngredientName().equals(ingredientName)) {
                            parts.remove(i);
                        }
                    }
                }
                ingredientList.remove(ingredientIndex);
                UI.printIngrNameRemoved(ingredientName);
                assert ingredientList.size() == (listSize - 1) : "ingredientList should be of size N-1";
            } else {
                UI.printDisregardMsg();
            }
        }
    }

    /**
     * Clears the ingredient list
     *
     * @author Rakesh12000
     */
    public static void clearList() {
        Scanner input = new Scanner(System.in);
        UI.printConfirmClearIngr();
        String confirmClear = input.nextLine().toLowerCase();

        while (!confirmClear.matches(YES_NO_REGEX)) {
            UI.clearTerminalAndPrintNewPage();
            UI.printInvalidConfirmation();
            confirmClear = input.nextLine().toLowerCase();
        }
        UI.clearTerminalAndPrintNewPage();
        if (confirmClear.startsWith(YES)) {
            ingredientList.clear();
            assert ingredientList.size() == 0 : "ingredientList should be of size 0";
            UI.printIngrListCleared();
        } else {
            UI.printDisregardMsg();
        }
    }

    /**
     * Edits the name of one of the ingredients present in the list
     * @param ingredientIndex index of item to be edited
     * @throws FoodoramaException if new name is blank
     *
     * @author Rakesh12000
     */
    public static void editName(int ingredientIndex) throws FoodoramaException {
        String ingrName = ingredientList.get(ingredientIndex).getIngredientName();
        UI.printAskNewNameIngr(ingrName);

        Scanner input = new Scanner(System.in);
        String newName = input.nextLine().toLowerCase();
        while (isNumber(newName)) {
            UI.clearTerminalAndPrintNewPage();
            UI.printInvalidIngredientName();
            newName = input.nextLine().toLowerCase();
        }

        if (newName.isBlank()) {
            throw new FoodoramaException(UI.getBlankName("ingredient"));
        }

        UI.clearTerminalAndPrintNewPage();
        UI.printConfirmIngrEditMsg(ingrName, newName);
        String confirmChange = input.nextLine().toLowerCase();
        while (!confirmChange.matches(YES_NO_REGEX)) {
            UI.clearTerminalAndPrintNewPage();
            UI.printInvalidConfirmation();
            confirmChange = input.nextLine().toLowerCase();
        }
        UI.clearTerminalAndPrintNewPage();
        if (confirmChange.startsWith(YES)) {
            ingredientList.get(ingredientIndex).setIngredientName(newName);
            UI.printIngrNameChanged(ingrName, newName);
        } else {
            UI.printDisregardMsg();
        }
    }

    /**
     * Check if the given date matches the proper date formatting
     * @param expiryDateString date string to be checked
     * @return true if follows the correct format, false otherwise
     *
     * @author renzocanare
     */
    private static boolean isValidDateFormat(String expiryDateString) {
        try {
            LocalDate.parse(expiryDateString, dtf);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    /**
     * Checks if the expiry date is too far away in the future
     * or in the past and prompts the user if that's the case
     * @param daysBetweenExpiryToday time till the expiry date
     * @param ingrName name of the ingredient its getting expiry updated
     * @return false if expiry is in the past or too far away in the future, true otherwise
     *
     * @author renzocanare
     */
    public static boolean isValidExpiryLength(long daysBetweenExpiryToday, String ingrName) {
        if (daysBetweenExpiryToday > TEN_YEARS_IN_DAYS) {
            UI.printLongExpiryDateMsg();
            Scanner input = new Scanner(System.in);
            String confirmDate = input.nextLine().toLowerCase();
            while (!confirmDate.matches(YES_NO_REGEX)) {
                UI.clearTerminalAndPrintNewPage();
                UI.printInvalidConfirmation();
                confirmDate = input.nextLine().toLowerCase();
            }
            UI.clearTerminalAndPrintNewPage();
            if (confirmDate.startsWith("n")) {
                UI.printAskIngrExpiryDate(ingrName);
                return false;
            }
        } else if (daysBetweenExpiryToday < ZERO_DAYS) {
            UI.printPassedExpiryDateMsg();
            return false;
        }
        return true;
    }

    /**
     * Add expiry date for an ingredient
     * @param ingredientIndex index of the ingredient expiry is being added to
     *
     * @author renzocanare
     */
    public static void addExpiry(int ingredientIndex) {
        String ingrName = ingredientList.get(ingredientIndex).getIngredientName();
        UI.printAskIngrExpiryDate(ingrName);

        Scanner input = new Scanner(System.in);
        String expiryDateString = input.nextLine();
        LocalDate expiryDate = null;

        int exitLoop = 0;
        long daysBetweenExpiryToday = Long.MIN_VALUE;
        while (exitLoop == 0) {
            UI.clearTerminalAndPrintNewPage();
            if (!isValidDateFormat(expiryDateString)) {
                UI.printIncorrectExpiryDateFormatMsg();
            } else if (isValidDateFormat(expiryDateString)) {
                expiryDate = LocalDate.parse(expiryDateString, dtf);
                daysBetweenExpiryToday = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
            }
            if (isValidDateFormat(expiryDateString) && daysBetweenExpiryToday != Long.MIN_VALUE
                    && isValidExpiryLength(daysBetweenExpiryToday, ingrName)) {
                exitLoop = 1;
            } else {
                expiryDateString = input.nextLine();
            }
        }
        ingredientList.get(ingredientIndex).setExpiryDate(expiryDate);
        UI.clearTerminalAndPrintNewPage();
        UI.printSetIngrExpiryDate(ingrName, expiryDate, daysBetweenExpiryToday);
    }

    /**
     * Edits the wastage of one of the ingredients present in the list
     * @param ingrIndex index of item to be edited
     * @throws FoodoramaException if new wastage is negative or infinity
     *
     * @author renzocanare
     */
    public static void editWastage(int ingrIndex) throws FoodoramaException {

        String ingrName = ingredientList.get(ingrIndex).getIngredientName();
        UI.printAskNewWastageDish(ingrName);

        Scanner input = new Scanner(System.in);
        double newWeight;

        try {
            newWeight = Double.parseDouble(input.nextLine());
            if (newWeight < 0) {
                throw new FoodoramaException("");
            }
        } catch (NumberFormatException | FoodoramaException e) {
            throw new FoodoramaException(UI.getInvalidNumberMsg());
        }
        if (Double.isInfinite(newWeight) | Double.isNaN(newWeight)) {
            throw new FoodoramaException(UI.printNumericalInputInvalid("ingredient waste"));
        }
        Double ingrWeight = ingredientList.get(ingrIndex).getWastage();

        UI.clearTerminalAndPrintNewPage();
        UI.printConfirmDishWastageEditMsg(ingrWeight, newWeight);
        String confirmChange = input.nextLine().toLowerCase();
        while (!confirmChange.matches(YES_NO_REGEX)) {
            UI.clearTerminalAndPrintNewPage();
            UI.printInvalidConfirmation();
            confirmChange = input.nextLine().toLowerCase();
        }
        UI.clearTerminalAndPrintNewPage();
        if (confirmChange.startsWith(YES)) {
            ingredientList.get(ingrIndex).setIngredientWaste(newWeight);
            UI.printDishWastageChanged(ingrName, newWeight);
        } else {
            UI.printDisregardMsg();
        }
    }

    /**
     * Edits the storage of one of the ingredients present in the list
     * @param ingrIndex index of item to be edited
     * @throws FoodoramaException if new storage is negative or infinity
     *
     * @author renzocanare
     */
    public static void editStorage(int ingrIndex) throws FoodoramaException {
        if (ingrIndex == -1) {
            throw new FoodoramaException(UI.getIngrNotExistEdit());
        } else if (ingrIndex < 0 || ingrIndex >= IngredientList.ingredientList.size()) {
            throw new FoodoramaException(UI.getIngrIndexExceedSizeMsg());
        } else {
            String ingrName = ingredientList.get(ingrIndex).getIngredientName();
            UI.printAskNewStorageIngr(ingrName);

            Scanner input = new Scanner(System.in);
            double newWeight;

            try {
                newWeight = Double.parseDouble(input.nextLine());
                if (newWeight < 0) {
                    throw new FoodoramaException("");
                }
            } catch (NumberFormatException | FoodoramaException e) {
                throw new FoodoramaException(UI.getInvalidNumberMsg());
            }
            if (Double.isInfinite(newWeight) | Double.isNaN(newWeight)) {
                throw new FoodoramaException(UI.printNumericalInputInvalid("ingredient storage"));
            }
            Double ingrWeight = ingredientList.get(ingrIndex).getIngredientWeight();

            UI.clearTerminalAndPrintNewPage();
            UI.printConfirmIngrStorageEditMsg(ingrWeight, newWeight);
            String confirmChange = input.nextLine().toLowerCase();
            while (!confirmChange.matches(YES_NO_REGEX)) {
                UI.clearTerminalAndPrintNewPage();
                UI.printInvalidConfirmation();
                confirmChange = input.nextLine().toLowerCase();
            }
            UI.clearTerminalAndPrintNewPage();
            if (confirmChange.startsWith(YES)) {
                ingredientList.get(ingrIndex).setIngredientWeight(newWeight);
                UI.printIngrStorageChanged(ingrName, newWeight);
            } else {
                UI.printDisregardMsg();
            }
        }
    }

    /**
     * Checks if given string can be converted into a number
     * @param numberString string to be checked
     * @return true if string can be converted into a double, false otherwise
     *
     * @author Rakesh12000
     */
    public static boolean isNumber(String numberString) {
        try {
            double numberInteger = Double.parseDouble(numberString);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Get user confirmation through a prompt
     * @param confirmAdd user input as a string
     * @return final user input if it is not invalid
     *
     * @author Rakesh12000
     */
    public static String getConfirmation(String confirmAdd) {
        Scanner input = new Scanner(System.in);
        while (!confirmAdd.matches(YES_NO_REGEX)) {
            UI.clearTerminalAndPrintNewPage();
            UI.printInvalidConfirmationSoftLimit();
            confirmAdd = input.nextLine().toLowerCase();
        }
        return confirmAdd;
    }
}
