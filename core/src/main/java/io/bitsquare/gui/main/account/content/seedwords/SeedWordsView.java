/*
 * This file is part of Bitsquare.
 *
 * Bitsquare is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bitsquare is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bitsquare. If not, see <http://www.gnu.org/licenses/>.
 */

package io.bitsquare.gui.main.account.content.seedwords;

import io.bitsquare.gui.main.help.Help;
import io.bitsquare.gui.main.help.HelpId;
import io.bitsquare.common.viewfx.view.FxmlView;
import io.bitsquare.common.viewfx.view.InitializableView;
import io.bitsquare.common.viewfx.view.Wizard;

import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;

@FxmlView
public class SeedWordsView extends InitializableView<GridPane, SeedWordsViewModel> implements Wizard.Step {

    @FXML Button completedButton;
    @FXML TextArea seedWordsTextArea;

    private Wizard wizard;

    @Inject
    private SeedWordsView(SeedWordsViewModel model) {
        super(model);
    }

    @Override
    public void initialize() {
        seedWordsTextArea.setText(model.seedWords.get());
    }

    @Override
    public void setWizard(Wizard wizard) {
        this.wizard = wizard;
    }

    @Override
    public void hideWizardNavigation() {
        root.getChildren().remove(completedButton);
    }

    @FXML
    private void onCompleted() {
        if (wizard != null)
            wizard.nextStep(this);
    }

    @FXML
    private void onOpenHelp() {
        Help.openWindow(HelpId.SETUP_SEED_WORDS);
    }
}

