import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

class BankAccount {
    private String accountNumber;
    private double balance;

    public BankAccount(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    public synchronized void deposit(double amount) {
        balance += amount;
    }

    public synchronized void withdraw(double amount) {
        if (balance >= amount) {
            balance -= amount;
        } else {
            System.out.println("Insufficient funds!");
        }
    }

    public synchronized double getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}

class TransactionLog {
    public static synchronized void logTransaction(String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = dateFormat.format(new Date());
        System.out.println(timestamp + ": " + message);
    }
}

class BankGUI extends JFrame {
    private JTextField accountNumberField;
    private JTextField amountField;
    private JTextArea logArea;
    private BankAccount currentAccount;

    public BankGUI() {
        setTitle("Multi-Threaded Banking System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        accountNumberField = new JTextField(20);
        amountField = new JTextField(10);
        logArea = new JTextArea(10, 30);
        logArea.setEditable(false);
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton balanceButton = new JButton("Check Balance");
        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(accountNumberField.getText().equals(currentAccount.getAccountNumber())) {
                performTransaction("Deposit", Double.parseDouble(amountField.getText()));
                System.out.println(Double.parseDouble(accountNumberField.getText()));
                }
                else {
                    System.out.println("Please enter correct account number to deposit.");
                }
            }
        });
        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(accountNumberField.getText().equals(currentAccount.getAccountNumber())) {
                performTransaction("Withdraw", Double.parseDouble(amountField.getText()));
                }
                else {
                    System.out.println("Please enter correct account number to withdraw");
                }
            }
        });
        balanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(accountNumberField.getText().equals(currentAccount.getAccountNumber())) {
                logTransaction("Balance: " + currentAccount.getBalance());
                }
                else {
                    System.out.println("Please enter correct account number to check balance.");
                }
            }
        });
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));
        panel.add(new JLabel("Account Number:"));
        panel.add(accountNumberField);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(depositButton);
        panel.add(withdrawButton);
        panel.add(balanceButton);
        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
        setVisible(true);
    }

    void performTransaction(String transactionType, double amount) {
        if (currentAccount == null) {
            logTransaction("No account selected.");
            return;
        }
        synchronized (currentAccount) {
            if (transactionType.equals("Deposit")) {
                currentAccount.deposit(amount);
                logTransaction("Deposit of $" + amount + " into account " + currentAccount.getAccountNumber());
            } else if (transactionType.equals("Withdraw")) {
                currentAccount.withdraw(amount);
                logTransaction("Withdrawal of $" + amount + " from account " + currentAccount.getAccountNumber());
            }
        }
    }

    void logTransaction(String message) {
        TransactionLog.logTransaction(message);
        logArea.append(message + "\n");
    }

    public void setCurrentAccount(BankAccount account) {
        this.currentAccount = account;
    }
}

public class BankingSystem {
    public static void main(String[] args) {
        BankAccount account1 = new BankAccount("123456", 1000);
        BankAccount account2 = new BankAccount("789012", 500);
        BankGUI gui = new BankGUI();
        gui.setCurrentAccount(account1);
        Thread thread1 = new Thread(() -> {
            gui.setCurrentAccount(account1);
            gui.logTransaction("Thread 1 started");
            gui.performTransaction("Deposit", 200);
            gui.performTransaction("Withdraw", 100);
            gui.logTransaction("Thread 1 finished");
        });
        Thread thread2 = new Thread(() -> {
            gui.setCurrentAccount(account2);
            gui.logTransaction("Thread 2 started");
            gui.performTransaction("Deposit", 300);
            gui.performTransaction("Withdraw", 50);
            gui.logTransaction("Thread 2 finished");
        });
        thread1.start();
        thread2.start();
    }
}