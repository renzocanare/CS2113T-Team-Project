package seedu.foodorama.command;

import seedu.foodorama.DishList;
import seedu.foodorama.Ui;
import seedu.foodorama.exceptions.FoodoramaException;
import seedu.foodorama.logger.LoggerManager;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LinkCommand extends Command {
    private static final Logger LOGGER = Logger.getLogger("AddingDishIngrCommand.execute()");
    private static final Ui UI = new Ui();

    LinkCommand() {
        LoggerManager.setupLogger(LOGGER);
    }

    /**
     * Checks if the input parameters of the link command are valid before
     * calling the appropriate function in the DishList class
     * @param parameters parameters for the link command
     * @throws FoodoramaException if the dish input doesn't exist in the list of dishes
     *
     * @author Dniv-ra
     */
    @Override
    public void execute(ArrayList<String> parameters) throws FoodoramaException {
        LOGGER.log(Level.INFO, "Start of process");
        int dishIndex = DishList.find(parameters.get(0));

        if (dishIndex == -1) {
            LOGGER.log(Level.INFO, "Dish does not exist", dishIndex);
            throw new FoodoramaException(UI.getDishNotExistMsg(parameters.get(0)));
        } else {
            DishList.dishList.get(dishIndex).addPart(parameters.get(1));
            LOGGER.log(Level.INFO, "Successfully added dish ingredient");
        }
        LOGGER.log(Level.INFO, "End of process");
    }
}
