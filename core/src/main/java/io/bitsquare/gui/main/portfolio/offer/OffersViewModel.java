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

package io.bitsquare.gui.main.portfolio.offer;

import io.bitsquare.gui.components.Popups;
import io.bitsquare.gui.util.BSFormatter;
import io.bitsquare.offer.Offer;
import io.bitsquare.common.viewfx.model.ActivatableWithDataModel;
import io.bitsquare.common.viewfx.model.ViewModel;

import com.google.inject.Inject;

import javafx.collections.ObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class OffersViewModel extends ActivatableWithDataModel<OffersDataModel> implements ViewModel {
    private static final Logger log = LoggerFactory.getLogger(OffersViewModel.class);

    private final BSFormatter formatter;


    @Inject
    public OffersViewModel(OffersDataModel dataModel, BSFormatter formatter) {
        super(dataModel);

        this.formatter = formatter;
    }


    void removeOpenOffer(Offer offer) {
        dataModel.removeOpenOffer(offer,
                () -> {
                    // visual feedback?
                    log.debug("Remove offer was successful");
                },
                (message) -> {
                    log.error(message);
                    Popups.openWarningPopup("Remove offer failed", message);
                });
    }

    public ObservableList<OfferListItem> getList() {
        return dataModel.getList();
    }

    String getTradeId(OfferListItem item) {
        return item.getOffer().getId();
    }

    String getAmount(OfferListItem item) {
        return (item != null) ? formatter.formatAmountWithMinAmount(item.getOffer()) : "";
    }

    String getPrice(OfferListItem item) {
        return (item != null) ? formatter.formatFiat(item.getOffer().getPrice()) : "";
    }

    String getVolume(OfferListItem item) {
        return (item != null) ? formatter.formatVolumeWithMinVolume(item.getOffer()) : "";
    }

    String getDirectionLabel(OfferListItem item) {
        return (item != null) ? formatter.formatDirection(dataModel.getDirection(item.getOffer())) : "";
    }

    String getDate(OfferListItem item) {
        return formatter.formatDateTime(item.getOffer().getCreationDate());
    }

}
