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

package io.bitsquare.trade.protocol.trade.taker.models;

import io.bitsquare.btc.BlockChainService;
import io.bitsquare.btc.WalletService;
import io.bitsquare.crypto.SignatureService;
import io.bitsquare.network.Peer;
import io.bitsquare.persistence.Persistence;
import io.bitsquare.trade.Trade;
import io.bitsquare.trade.TradeMessageService;
import io.bitsquare.trade.protocol.trade.SharedTradeModel;
import io.bitsquare.user.User;

import org.bitcoinj.core.Transaction;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SellerAsTakerModel extends SharedTradeModel implements Serializable {
    private static final long serialVersionUID = -963501132927618376L;
    private static final Logger log = LoggerFactory.getLogger(SellerAsTakerModel.class);

    public final Trade trade;
    public final TakerModel taker;
    public final OffererModel offerer;

    // written by tasks
    private Transaction takeOfferFeeTx;
    private Transaction payoutTx;

    public SellerAsTakerModel(Trade trade,
                              Peer offererPeer,
                              TradeMessageService tradeMessageService,
                              WalletService walletService,
                              BlockChainService blockChainService,
                              SignatureService signatureService,
                              User user,
                              Persistence persistence) {
        super(trade.getOffer(),
                tradeMessageService,
                walletService,
                blockChainService,
                signatureService,
                persistence);

        this.trade = trade;

        Serializable serializable = persistence.read(this, "SellerAsTakerModel_" + id);
        if (serializable instanceof SellerAsTakerModel) {
            SellerAsTakerModel persistedModel = (SellerAsTakerModel) serializable;
            log.debug("Model reconstructed form persisted model.");

            setTakeOfferFeeTx(persistedModel.getTakeOfferFeeTx());
            setPayoutTx(persistedModel.payoutTx);

            taker = persistedModel.taker;
            offerer = persistedModel.offerer;
        }
        else {
            taker = new TakerModel();
            offerer = new OffererModel();
        }

        offerer.peer = offererPeer;

        taker.registrationPubKey = walletService.getRegistrationAddressEntry().getPubKey();
        taker.registrationKeyPair = walletService.getRegistrationAddressEntry().getKeyPair();
        taker.addressEntry = walletService.getAddressEntry(id);
        taker.fiatAccount = user.getBankAccount(offer.getBankAccountId());
        taker.accountId = user.getAccountId();
        taker.messagePubKey = user.getMessagePubKey();
        taker.pubKey = taker.addressEntry.getPubKey();
    }

    // Get called form taskRunner after each completed task
    @Override
    public void persist() {
        persistence.write(this, "SellerAsTakerModel_" + id, this);
    }

    public Transaction getTakeOfferFeeTx() {
        return takeOfferFeeTx;
    }

    public void setTakeOfferFeeTx(Transaction takeOfferFeeTx) {
        this.takeOfferFeeTx = takeOfferFeeTx;
    }

    public Transaction getPayoutTx() {
        return payoutTx;
    }

    public void setPayoutTx(Transaction payoutTx) {
        this.payoutTx = payoutTx;
    }
}
