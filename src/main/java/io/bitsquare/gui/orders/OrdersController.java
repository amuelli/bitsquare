package io.bitsquare.gui.orders;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.core.WalletEventListener;
import com.google.bitcoin.script.Script;
import com.google.inject.Inject;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import io.bitsquare.bank.BankAccount;
import io.bitsquare.bank.BankAccountType;
import io.bitsquare.btc.WalletFacade;
import io.bitsquare.gui.ChildController;
import io.bitsquare.gui.NavigationController;
import io.bitsquare.gui.util.ConfidenceDisplay;
import io.bitsquare.gui.util.Icons;
import io.bitsquare.gui.util.Localisation;
import io.bitsquare.trade.Direction;
import io.bitsquare.trade.Offer;
import io.bitsquare.trade.Trade;
import io.bitsquare.trade.Trading;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.URL;
import java.util.*;

public class OrdersController implements Initializable, ChildController
{
    private static final Logger log = LoggerFactory.getLogger(OrdersController.class);

    private Trading trading;
    private WalletFacade walletFacade;
    private Trade currentTrade;

    private Image buyIcon = Icons.getIconImage(Icons.BUY);
    private Image sellIcon = Icons.getIconImage(Icons.SELL);
    private ConfidenceDisplay confidenceDisplay;

    @FXML
    private TableView openTradesTable;
    @FXML
    private TableColumn<String, TradesTableItem> directionColumn, countryColumn, bankAccountTypeColumn, priceColumn, amountColumn, volumeColumn, statusColumn, selectColumn;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label confirmationLabel, txIDCopyIcon, holderNameCopyIcon, primaryBankAccountIDCopyIcon, secondaryBankAccountIDCopyIcon;
    @FXML
    private TextField txTextField, bankAccountTypeTextField, holderNameTextField, primaryBankAccountIDTextField, secondaryBankAccountIDTextField;
    @FXML
    private Button bankTransferInitedButton;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Inject
    public OrdersController(Trading trading, WalletFacade walletFacade)
    {
        this.trading = trading;
        this.walletFacade = walletFacade;
    }


    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        Map<String, Trade> trades = trading.getTrades();
        List<Trade> tradeList = new ArrayList<>(trades.values());
        ObservableList<TradesTableItem> tradeItems = FXCollections.observableArrayList();
        for (Iterator<Trade> iterator = tradeList.iterator(); iterator.hasNext(); )
        {
            Trade trade = iterator.next();
            tradeItems.add(new TradesTableItem(trade));
        }

        setCountryColumnCellFactory();
        setBankAccountTypeColumnCellFactory();
        setDirectionColumnCellFactory();
        setSelectColumnCellFactory();

        openTradesTable.setItems(tradeItems);
        openTradesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        openTradesTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue instanceof TradesTableItem)
            {
                showTradeDetails((TradesTableItem) newValue);
            }
        });

        trading.getNewTradeProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldTradeUid, String newTradeUid)
            {
                Trade newTrade = trading.getTrades().get(newTradeUid);
                tradeItems.add(new TradesTableItem(newTrade));
            }
        });

        initCopyIcons();

        if (tradeItems.size() > 0)
        {
            openTradesTable.getSelectionModel().select(0);
        }

        tradeItems.addListener(new ListChangeListener<TradesTableItem>()
        {
            @Override
            public void onChanged(Change<? extends TradesTableItem> change)
            {
                if (openTradesTable.getSelectionModel().getSelectedItem() == null && tradeItems.size() > 0)
                {
                    openTradesTable.getSelectionModel().select(0);
                }

            }
        });
    }

    private void showTradeDetails(TradesTableItem tradesTableItem)
    {
        fillData(tradesTableItem.getTrade());
    }

    @Override
    public void setNavigationController(NavigationController navigationController)
    {
    }

    @Override
    public void cleanup()
    {
    }

    public void bankTransferInited(ActionEvent actionEvent)
    {
        trading.onBankTransferInited(currentTrade.getUid());
        bankTransferInitedButton.setDisable(true);
    }

    private void updateTx(Trade trade)
    {
        Transaction transaction = trade.getDepositTransaction();

        if (transaction != null)
        {
            confirmationLabel.setVisible(true);
            progressIndicator.setVisible(true);
            progressIndicator.setProgress(-1);

            txTextField.setText(transaction.getHashAsString());

            confidenceDisplay = new ConfidenceDisplay(walletFacade.getWallet(), confirmationLabel, transaction, progressIndicator);

            int depthInBlocks = transaction.getConfidence().getDepthInBlocks();
            bankTransferInitedButton.setDisable(depthInBlocks == 0);

            walletFacade.getWallet().addEventListener(new WalletEventListener()
            {
                @Override
                public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx)
                {
                    int depthInBlocks = tx.getConfidence().getDepthInBlocks();
                    bankTransferInitedButton.setDisable(depthInBlocks == 0);
                }

                @Override
                public void onCoinsReceived(Wallet wallet, Transaction tx, BigInteger prevBalance, BigInteger newBalance)
                {
                }

                @Override
                public void onCoinsSent(Wallet wallet, Transaction tx, BigInteger prevBalance, BigInteger newBalance)
                {
                }

                @Override
                public void onReorganize(Wallet wallet)
                {
                }


                @Override
                public void onWalletChanged(Wallet wallet)
                {
                }

                @Override
                public void onKeysAdded(Wallet wallet, List<ECKey> keys)
                {
                }

                @Override
                public void onScriptsAdded(Wallet wallet, List<Script> scripts)
                {
                }
            });

        }
    }


    private void fillData(Trade trade)
    {
        currentTrade = trade;
        Transaction transaction = trade.getDepositTransaction();
        if (transaction == null)
        {
            trade.getDepositTxChangedProperty().addListener(new ChangeListener<Boolean>()
            {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2)
                {
                    updateTx(trade);
                }
            });
        }
        else
        {
            updateTx(trade);
        }

        // back details
        if (trade.getContract() != null)
        {
            setBankData(trade);
        }
        else
        {
            trade.getContractChangedProperty().addListener(new ChangeListener<Boolean>()
            {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2)
                {
                    setBankData(trade);
                }
            });
        }
    }

    private void setBankData(Trade trade)
    {
        BankAccount bankAccount = trade.getContract().getTakerBankAccount();
        //TODO why null?
        if (bankAccount != null)
        {
            bankAccountTypeTextField.setText(bankAccount.getBankAccountType().getType().toString());
            holderNameTextField.setText(bankAccount.getAccountHolderName());
            primaryBankAccountIDTextField.setText(bankAccount.getAccountPrimaryID());
            secondaryBankAccountIDTextField.setText(bankAccount.getAccountSecondaryID());
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Table columns
    ///////////////////////////////////////////////////////////////////////////////////////////

    private void setCountryColumnCellFactory()
    {
        countryColumn.setCellValueFactory((offer) -> new ReadOnlyObjectWrapper(offer.getValue()));
        countryColumn.setCellFactory(new Callback<TableColumn<String, TradesTableItem>, TableCell<String, TradesTableItem>>()
        {
            @Override
            public TableCell<String, TradesTableItem> call(TableColumn<String, TradesTableItem> directionColumn)
            {
                return new TableCell<String, TradesTableItem>()
                {
                    final HBox hBox = new HBox();

                    {
                        hBox.setSpacing(3);
                        hBox.setAlignment(Pos.CENTER);
                        setGraphic(hBox);
                    }

                    @Override
                    public void updateItem(final TradesTableItem tradesTableItem, boolean empty)
                    {
                        super.updateItem(tradesTableItem, empty);

                        hBox.getChildren().clear();
                        if (tradesTableItem != null)
                        {
                            Locale countryLocale = tradesTableItem.getTrade().getOffer().getBankAccountCountryLocale();
                            try
                            {
                                hBox.getChildren().add(Icons.getIconImageView("/images/countries/" + countryLocale.getCountry().toLowerCase() + ".png"));

                            } catch (Exception e)
                            {
                                log.warn("Country icon not found: " + "/images/countries/" + countryLocale.getCountry().toLowerCase() + ".png country name: " + countryLocale.getDisplayCountry());
                            }
                            Tooltip.install(this, new Tooltip(countryLocale.getDisplayCountry()));
                        }
                    }
                };
            }
        });
    }

    private void setBankAccountTypeColumnCellFactory()
    {
        bankAccountTypeColumn.setCellValueFactory((offer) -> new ReadOnlyObjectWrapper(offer.getValue()));
        bankAccountTypeColumn.setCellFactory(new Callback<TableColumn<String, TradesTableItem>, TableCell<String, TradesTableItem>>()
        {
            @Override
            public TableCell<String, TradesTableItem> call(TableColumn<String, TradesTableItem> directionColumn)
            {
                return new TableCell<String, TradesTableItem>()
                {
                    @Override
                    public void updateItem(final TradesTableItem tradesTableItem, boolean empty)
                    {
                        super.updateItem(tradesTableItem, empty);

                        if (tradesTableItem != null)
                        {
                            BankAccountType.BankAccountTypeEnum bankAccountTypeEnum = tradesTableItem.getTrade().getOffer().getBankAccountTypeEnum();
                            setText(Localisation.get(bankAccountTypeEnum.toString()));
                        }
                        else
                        {
                            setText("");
                        }
                    }
                };
            }
        });
    }

    private void setDirectionColumnCellFactory()
    {
        directionColumn.setCellValueFactory((offer) -> new ReadOnlyObjectWrapper(offer.getValue()));
        directionColumn.setCellFactory(new Callback<TableColumn<String, TradesTableItem>, TableCell<String, TradesTableItem>>()
        {
            @Override
            public TableCell<String, TradesTableItem> call(TableColumn<String, TradesTableItem> directionColumn)
            {
                return new TableCell<String, TradesTableItem>()
                {
                    final ImageView iconView = new ImageView();
                    final Button button = new Button();

                    {
                        button.setGraphic(iconView);
                        button.setMinWidth(70);
                    }

                    @Override
                    public void updateItem(final TradesTableItem tradesTableItem, boolean empty)
                    {
                        super.updateItem(tradesTableItem, empty);

                        if (tradesTableItem != null)
                        {
                            String title;
                            Image icon;
                            Offer offer = tradesTableItem.getTrade().getOffer();

                            if (offer.getDirection() == Direction.SELL)
                            {
                                icon = buyIcon;
                                title = io.bitsquare.gui.util.Formatter.formatDirection(Direction.BUY, true);
                            }
                            else
                            {
                                icon = sellIcon;
                                title = io.bitsquare.gui.util.Formatter.formatDirection(Direction.SELL, true);
                            }
                            button.setDisable(true);
                            iconView.setImage(icon);
                            button.setText(title);
                            setGraphic(button);
                        }
                        else
                        {
                            setGraphic(null);
                        }
                    }
                };
            }
        });
    }

    private void setSelectColumnCellFactory()
    {
        selectColumn.setCellValueFactory((offer) -> new ReadOnlyObjectWrapper(offer.getValue()));
        selectColumn.setCellFactory(new Callback<TableColumn<String, TradesTableItem>, TableCell<String, TradesTableItem>>()
        {
            @Override
            public TableCell<String, TradesTableItem> call(TableColumn<String, TradesTableItem> directionColumn)
            {
                return new TableCell<String, TradesTableItem>()
                {
                    final Button button = new Button("Select");

                    @Override
                    public void updateItem(final TradesTableItem tradesTableItem, boolean empty)
                    {
                        super.updateItem(tradesTableItem, empty);

                        if (tradesTableItem != null)
                        {
                            button.setOnAction(event -> showTradeDetails(tradesTableItem));
                            setGraphic(button);
                        }
                        else
                        {
                            setGraphic(null);
                        }
                    }
                };
            }
        });
    }


    private void initCopyIcons()
    {
        AwesomeDude.setIcon(txIDCopyIcon, AwesomeIcon.COPY);
        txIDCopyIcon.setOnMouseClicked(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(txTextField.getText());
            clipboard.setContent(content);
        });

        AwesomeDude.setIcon(holderNameCopyIcon, AwesomeIcon.COPY);
        holderNameCopyIcon.setOnMouseClicked(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(holderNameTextField.getText());
            clipboard.setContent(content);
        });

        AwesomeDude.setIcon(primaryBankAccountIDCopyIcon, AwesomeIcon.COPY);
        primaryBankAccountIDCopyIcon.setOnMouseClicked(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(primaryBankAccountIDTextField.getText());
            clipboard.setContent(content);
        });

        AwesomeDude.setIcon(secondaryBankAccountIDCopyIcon, AwesomeIcon.COPY);
        secondaryBankAccountIDCopyIcon.setOnMouseClicked(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(secondaryBankAccountIDTextField.getText());
            clipboard.setContent(content);
        });
    }

}
