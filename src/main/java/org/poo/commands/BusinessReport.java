package org.poo.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.banksystem.Account;
import org.poo.banksystem.BusinessAccount;
import org.poo.banksystem.Commerciant;
import org.poo.banksystem.User;
import org.poo.fileio.CommandInput;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class BusinessReport implements Command {
    private final List<User> users;

    /**
     * Constructor for BusinessReport.
     * @param users the users
     */
    public BusinessReport(final List<User> users) {
        this.users = users;
    }

    /**
     * This method is used to generate a business report.
     * @param command the command to be executed
     * @param objectMapper the object mapper
     * @param output the output array
     */
    @Override
    public void execute(final CommandInput command, final ObjectMapper objectMapper,
                        final ArrayNode output) {
        Account account = findAccountByIBAN(command.getAccount());
        if (account == null) {
            ObjectNode errorNode = objectMapper.createObjectNode();
            errorNode.put("command", "businessReport");
            errorNode.put("timestamp", command.getTimestamp());
            errorNode.putObject("output").put("description", "Account not found")
                    .put("timestamp", command.getTimestamp());
            output.add(errorNode);
            return;
        }
        if (!account.getType().equals("business")) {
            ObjectNode errorNode = objectMapper.createObjectNode();
            errorNode.put("command", "businessReport");
            errorNode.put("timestamp", command.getTimestamp());
            errorNode.putObject("output").put("description", "This is not a business account")
                    .put("timestamp", command.getTimestamp());
            output.add(errorNode);
            return;
        }
        BusinessAccount businessAccount = (BusinessAccount) account;
        ObjectNode reportNode = objectMapper.createObjectNode();
        reportNode.put("command", "businessReport");
        reportNode.put("timestamp", command.getTimestamp());

        ObjectNode outputNode = reportNode.putObject("output");
        outputNode.put("IBAN", businessAccount.getIBAN());
        outputNode.put("balance", businessAccount.getBalance());
        outputNode.put("currency", businessAccount.getCurrency());
        outputNode.put("spending limit", businessAccount.getSpendingLimit());
        outputNode.put("deposit limit", businessAccount.getDepositLimit());
        outputNode.put("statistics type", command.getType());

        if (command.getType().equals("transaction")) {
            generateTransactionReport(businessAccount, outputNode, objectMapper,
                    command.getStartTimestamp(), command.getEndTimestamp());
        } else if (command.getType().equals("commerciant")) {
            generateCommerciantReport(businessAccount, outputNode, objectMapper,
                    command.getStartTimestamp(), command.getEndTimestamp());
        }

        output.add(reportNode);
    }

    /**
     * This method is used to generate a transaction report.
     * @param businessAccount the business account
     * @param outputNode the output node
     * @param objectMapper the object mapper
     * @param startTimestamp the start timestamp
     * @param endTimestamp the end timestamp
     */
    private void generateTransactionReport(final BusinessAccount businessAccount,
                                           final ObjectNode outputNode,
                                           final ObjectMapper objectMapper,
                                           final int startTimestamp,
                                           final int endTimestamp) {
        ArrayNode managersArray = outputNode.putArray("managers");
        for (User manager : businessAccount.getManagers()) {
            ObjectNode managerNode = managersArray.addObject();
            managerNode.put("username", manager.getLastName() + " "
                    + manager.getFirstName());
            managerNode.put("spent", businessAccount.getManagerSpendings()
                    .getOrDefault(manager, 0.0));
            managerNode.put("deposited", businessAccount.getManagerDeposits()
                    .getOrDefault(manager, 0.0));
        }

        ArrayNode employeesArray = outputNode.putArray("employees");
        for (User employee : businessAccount.getEmployees()) {
            ObjectNode employeeNode = employeesArray.addObject();
            employeeNode.put("username", employee.getLastName() + " "
                    + employee.getFirstName());
            employeeNode.put("spent", businessAccount.getEmployeeSpendings()
                    .getOrDefault(employee, 0.0));
            employeeNode.put("deposited", businessAccount.getEmployeeDeposits()
                    .getOrDefault(employee, 0.0));
        }

        double totalSpent = businessAccount.getManagerSpendings().values().stream()
                .mapToDouble(Double::doubleValue)
                .sum()
                + businessAccount.getEmployeeSpendings().values().stream().
                mapToDouble(Double::doubleValue).
                sum();
        double totalDeposited = businessAccount.getManagerDeposits().values().stream().
                mapToDouble(Double::doubleValue)
                .sum()
                + businessAccount.getEmployeeDeposits().values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        outputNode.put("total spent", totalSpent);
        outputNode.put("total deposited", totalDeposited);
    }

    /**
     * This method is used to generate a commerciant report.
     * @param businessAccount the business account
     * @param outputNode the output node
     * @param objectMapper the object mapper
     * @param startTimestamp the start timestamp
     * @param endTimestamp the end timestamp
     */
    private void generateCommerciantReport(final BusinessAccount businessAccount,
                                           final ObjectNode outputNode,
                                           final ObjectMapper objectMapper,
                                           final int startTimestamp,
                                           final int endTimestamp) {
        ArrayNode commerciantsArray = outputNode.putArray("commerciants");

        businessAccount.getCommerciantPayments().entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Commerciant::getName)))
                .forEach(entry -> {
                    Commerciant commerciant = entry.getKey();
                    double totalReceived = entry.getValue();

                    ObjectNode commerciantNode = commerciantsArray.addObject();
                    commerciantNode.put("commerciant", commerciant.getName());
                    commerciantNode.put("total received", totalReceived);

                    ArrayNode managersArray = commerciantNode.putArray("managers");
                    List<User> managers = businessAccount.getCommerciantManagers().get(commerciant);
                    if (managers != null) {
                        managers.stream()
                                .sorted(Comparator.comparing(User::getLastName)
                                        .thenComparing(User::getFirstName))
                                .forEach(manager -> managersArray.add(manager.getLastName()
                                        + " " + manager.getFirstName()));
                    }

                    ArrayNode employeesArray = commerciantNode.putArray("employees");
                    List<User> employees = businessAccount.getCommerciantEmployees()
                            .get(commerciant);
                    if (employees != null) {
                        employees.stream()
                                .sorted(Comparator.comparing(User::getLastName)
                                        .thenComparing(User::getFirstName))
                                .forEach(employee -> employeesArray.add(employee.getLastName()
                                        + " " + employee.getFirstName()));
                    }
                });
    }

    /**
     * This method is used to find an account by IBAN.
     * @param iban the IBAN
     * @return the account
     */
    private Account findAccountByIBAN(final String iban) {
        for (User user : users) {
            for (Account account : user.getAccounts()) {
                if (account.getIBAN().equals(iban)) {
                    return account;
                }
            }
        }
        return null;
    }
}
