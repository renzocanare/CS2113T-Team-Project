package seedu.duke.command;

import seedu.duke.DishList;
import seedu.duke.Ui;

import java.util.ArrayList;

public class DeleteDishCommand extends Command {
    @Override
    public void execute(ArrayList<String> parameters) {
        DishList.delete(parameters.get(0));
    }
}
