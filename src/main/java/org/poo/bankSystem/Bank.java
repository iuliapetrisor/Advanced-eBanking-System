package org.poo.bankSystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.commands.AddAccount;
import org.poo.commands.AddFunds;
import org.poo.commands.CreateCard;
import org.poo.commands.CreateOneTimeCard;
import org.poo.commands.DeleteAccount;
import org.poo.commands.DeleteCard;
import org.poo.commands.PrintUsers;
import org.poo.commands.PayOnline;
import org.poo.commands.SendMoney;
import org.poo.commands.SetMinBalance;
import org.poo.commands.CheckCardStatus;
import org.poo.commands.SplitPayment;
import org.poo.commands.PrintTransactions;
import org.poo.commands.SetAlias;
import org.poo.commands.Report;
import org.poo.commands.SpendingsReport;
import org.poo.commands.AddInterest;
import org.poo.commands.ChangeInterestRate;
import org.poo.commands.WithdrawSavings;
import org.poo.transactions.TransactionManager;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ObjectInput;
import org.poo.fileio.UserInput;
import java.util.ArrayList;
import java.util.List;

public class Bank {
    private ObjectMapper objectMapper;
    private List<User> users = new ArrayList<>();
    private CommandInput[] commands;
    private final ExchangeRateManager exchangeRateManager;
    private PrintUsers printUsers;
    private AddAccount addAccount;
    private CreateCard createCard;
    private AddFunds addFunds;
    private DeleteAccount deleteAccount;
    private CreateOneTimeCard createOneTimeCard;
    private DeleteCard deleteCard;
    private PayOnline payOnline;
    private SendMoney sendMoney;
    private PrintTransactions printTransactions;
    private TransactionManager transactionManager;
    private SetMinBalance setMinBalance;
    private CheckCardStatus checkCardStatus;
    private SetAlias setAlias;
    private SplitPayment splitPayment;
    private Report report;
    private SpendingsReport spendingsReport;
    private AddInterest addInterest;
    private ChangeInterestRate changeInterestRate;
    private WithdrawSavings withdrawSavings;

    /**
     * Constructor for the Bank class.
     *
     * @param objectMapper the object mapper
     * @param inputData the object input
     */
    public Bank(final ObjectMapper objectMapper, final ObjectInput inputData) {
        for (UserInput userInput : inputData.getUsers()) {
            users.add(new User(userInput));
        }
        commands = inputData.getCommands();
        this.objectMapper = objectMapper;
        this.exchangeRateManager = new ExchangeRateManager(inputData.getExchangeRates());
        this.transactionManager = new TransactionManager(users);
        initializeCommands();
    }

    /**
     * This method is used to initialize the commands.
     */
    private void initializeCommands() {
        printUsers = new PrintUsers(users);
        addAccount = new AddAccount(users, transactionManager);
        createCard = new CreateCard(users, transactionManager);
        addFunds = new AddFunds(users);
        deleteAccount = new DeleteAccount(users, transactionManager);
        createOneTimeCard = new CreateOneTimeCard(users, transactionManager);
        deleteCard = new DeleteCard(users, transactionManager);
        payOnline = new PayOnline(users, exchangeRateManager, transactionManager);
        sendMoney = new SendMoney(users, exchangeRateManager, transactionManager);
        printTransactions = new PrintTransactions(users);
        setMinBalance = new SetMinBalance(users);
        checkCardStatus = new CheckCardStatus(users, transactionManager);
        setAlias = new SetAlias(users);
        splitPayment = new SplitPayment(users, exchangeRateManager, transactionManager);
        report = new Report(users);
        spendingsReport = new SpendingsReport(users);
        addInterest = new AddInterest(users);
        changeInterestRate = new ChangeInterestRate(users, transactionManager);
        withdrawSavings = new WithdrawSavings(users, exchangeRateManager, transactionManager);
    }

    /**
     * This method is used to execute the commands.
     * <p>
     * This method utilizes the Command design pattern to handle various operations
     * such as printing users, adding accounts, creating cards, and adding funds.
     * Each operation is encapsulated in a command object that implements the
     * {@link org.poo.commands.Command} interface.
     * </p>
     *
     * @param output the output array
     */
    public void executeCommands(final ArrayNode output) {
        for (CommandInput command : commands) {
            switch (command.getCommand()) {
                case "printUsers":
                    printUsers.execute(command, objectMapper, output);
                    break;
                case "addAccount":
                    addAccount.execute(command, objectMapper, output);
                    break;
                case "createCard":
                    createCard.execute(command, objectMapper, output);
                    break;
                case "createOneTimeCard":
                    createOneTimeCard.execute(command, objectMapper, output);
                    break;
                case "addFunds":
                    addFunds.execute(command, objectMapper, output);
                    break;
                case "deleteAccount":
                    deleteAccount.execute(command, objectMapper, output);
                    break;
                case "deleteCard":
                    deleteCard.execute(command, objectMapper, output);
                    break;
                case "payOnline":
                    payOnline.execute(command, objectMapper, output);
                    break;
                case "sendMoney":
                    sendMoney.execute(command, objectMapper, output);
                    break;
                case "printTransactions":
                    printTransactions.execute(command, objectMapper, output);
                    break;
                case "setMinimumBalance":
                    setMinBalance.execute(command, objectMapper, output);
                    break;
                case "checkCardStatus":
                    checkCardStatus.execute(command, objectMapper, output);
                    break;
                case "setAlias":
                    setAlias.execute(command, objectMapper, output);
                    break;
                case "splitPayment":
                    splitPayment.execute(command, objectMapper, output);
                    break;
                case "report":
                    report.execute(command, objectMapper, output);
                    break;
                case "spendingsReport":
                    spendingsReport.execute(command, objectMapper, output);
                    break;
                case "addInterest":
                    addInterest.execute(command, objectMapper, output);
                    break;
                case "changeInterestRate":
                    changeInterestRate.execute(command, objectMapper, output);
                    break;
                case "withdrawSavings":
                    withdrawSavings.execute(command, objectMapper, output);
                    break;
                default:
                    ObjectNode outputNode = objectMapper.createObjectNode();
                    outputNode.put("error", "Invalid command");
                    output.add(outputNode);
                    break;
            }
        }
    }
}
