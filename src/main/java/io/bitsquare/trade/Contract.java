package io.bitsquare.trade;

import io.bitsquare.bank.BankAccount;

import java.io.Serializable;
import java.math.BigInteger;

public class Contract implements Serializable
{
    private static final long serialVersionUID = 71472356206100158L;

    private Offer offer;
    private String takeOfferFeeTxID;
    private BigInteger tradeAmount;
    private String offererAccountID;
    private String takerAccountID;
    private BankAccount offererBankAccount;
    private BankAccount takerBankAccount;
    private String offererMessagePubKeyAsHex;
    private String takerMessagePubKeyAsHex;

    public Contract(Offer offer,
                    BigInteger tradeAmount,
                    String takeOfferFeeTxID,
                    String offererAccountID,
                    String takerAccountID,
                    BankAccount offererBankAccount,
                    BankAccount takerBankAccount,
                    String offererMessagePubKeyAsHex,
                    String takerMessagePubKeyAsHex)
    {
        this.offer = offer;
        this.tradeAmount = tradeAmount;
        this.takeOfferFeeTxID = takeOfferFeeTxID;
        this.offererAccountID = offererAccountID;
        this.takerAccountID = takerAccountID;
        this.offererBankAccount = offererBankAccount;
        this.takerBankAccount = takerBankAccount;
        this.offererMessagePubKeyAsHex = offererMessagePubKeyAsHex;
        this.takerMessagePubKeyAsHex = takerMessagePubKeyAsHex;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Getters
    ///////////////////////////////////////////////////////////////////////////////////////////

    public Offer getOffer()
    {
        return offer;
    }

    public String getTakeOfferFeeTxID()
    {
        return takeOfferFeeTxID;
    }

    public BigInteger getTradeAmount()
    {
        return tradeAmount;
    }

    public String getOffererAccountID()
    {
        return offererAccountID;
    }

    public String getTakerAccountID()
    {
        return takerAccountID;
    }

    public BankAccount getOffererBankAccount()
    {
        return offererBankAccount;
    }

    public BankAccount getTakerBankAccount()
    {
        return takerBankAccount;
    }

    public String getTakerMessagePubKeyAsHex()
    {
        return takerMessagePubKeyAsHex;
    }

    @Override
    public String toString()
    {
        return "Contract{" +
                "offer=" + offer +
                ", takeOfferFeeTxID='" + takeOfferFeeTxID + '\'' +
                ", tradeAmount=" + tradeAmount +
                ", offererAccountID='" + offererAccountID + '\'' +
                ", takerAccountID='" + takerAccountID + '\'' +
                ", offererBankAccount=" + offererBankAccount +
                ", takerBankAccount=" + takerBankAccount +
                ", offererMessagePubKeyAsHex='" + offererMessagePubKeyAsHex + '\'' +
                ", takerMessagePubKeyAsHex='" + takerMessagePubKeyAsHex + '\'' +
                '}';
    }

    public String getOffererMessagePubKeyAsHex()
    {
        return offererMessagePubKeyAsHex;
    }
}