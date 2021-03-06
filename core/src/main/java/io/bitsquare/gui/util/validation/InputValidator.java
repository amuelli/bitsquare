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

package io.bitsquare.gui.util.validation;

import io.bitsquare.locale.BSResources;

/**
 * Base class for other specialized validators.
 * <p/>
 * That class implements just what we need for the moment. It is not intended as a general purpose library class.
 */
public class InputValidator {

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Public methods
    ///////////////////////////////////////////////////////////////////////////////////////////

    public ValidationResult validate(String input) {
        return validateIfNotEmpty(input);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Protected methods
    ///////////////////////////////////////////////////////////////////////////////////////////

    protected ValidationResult validateIfNotEmpty(String input) {
        if (input == null || input.length() == 0)
            return new ValidationResult(false, BSResources.get("validation.empty"));
        else
            return new ValidationResult(true);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // ValidationResult
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static class ValidationResult {
        public final boolean isValid;
        public final String errorMessage;

        public ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }

        public ValidationResult(boolean isValid) {
            this(isValid, null);
        }

        public ValidationResult and(ValidationResult next) {
            if (this.isValid)
                return next;
            else
                return this;
        }

        @Override
        public String toString() {
            return "ValidationResult{" +
                    "isValid=" + isValid +
                    ", errorMessage='" + errorMessage + '\'' +
                    '}';
        }
    }
}
