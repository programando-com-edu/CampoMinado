import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class CampoMinado implements ActionListener {

    private static final int DEFAULT_SIZE = 8;
    private static final int DEFAULT_MINES = 10;

    private JButton[][] buttons;
    private Cell[][] board;
    private int boardSize;
    private int numMines;
    private int closedCells;
    private int score;

    private JFrame frame;
    private JLabel scoreLabel;

    public CampoMinado(int boardSize, int numMines) {
        this.boardSize = boardSize;
        this.numMines = numMines;
        this.closedCells = boardSize * boardSize;
        this.score = 0;

        frame = new JFrame("Campo Minado");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel gamePanel = new JPanel(new GridLayout(boardSize, boardSize));
        buttons = new JButton[boardSize][boardSize];
        board = new Cell[boardSize][boardSize];

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].addActionListener(this);
                gamePanel.add(buttons[i][j]);
                board[i][j] = new Cell();
            }
        }

        scoreLabel = new JLabel("Score: 0");
        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlPanel.add(scoreLabel);
        controlPanel.add(restartButton);

        frame.add(gamePanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        initializeGame();
    }

    private void initializeGame() {
        resetBoard();
        placeMines();
        countAdjacentMines();
        updateScore(0);
    }

    private void resetBoard() {
        closedCells = boardSize * boardSize;

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
                buttons[i][j].setBackground(null);
                board[i][j].setMine(false);
                board[i][j].setOpened(false);
                board[i][j].setAdjacentMines(0);
            }
        }
    }

    private void placeMines() {
        Random random = new Random();
        int minesPlaced = 0;

        while (minesPlaced < numMines) {
            int row = random.nextInt(boardSize);
            int col = random.nextInt(boardSize);

            if (!board[row][col].isMine()) {
                board[row][col].setMine(true);
                minesPlaced++;
            }
        }
    }

    private void countAdjacentMines() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (!board[i][j].isMine()) {
                    int count = 0;

                    for (int k = -1; k <= 1; k++) {
                        for (int l = -1; l <= 1; l++) {
                            int row = i + k;
                            int col = j + l;

                            if (isValidCell(row, col) && board[row][col].isMine()) {
                                count++;
                            }
                        }
                    }

                    board[i][j].setAdjacentMines(count);
                }
            }
        }
    }

    private void openCell(int row, int col) {
        if (!isValidCell(row, col) || board[row][col].isOpened()) {
            return;
        }

        JButton button = buttons[row][col];
        Cell cell = board[row][col];

        if (cell.isMine()) {
            button.setText("M");
            button.setEnabled(false);
            button.setBackground(Color.RED);
            gameOver();
        } else {
            int adjacentMines = cell.getAdjacentMines();

            if (adjacentMines > 0) {
                button.setText(String.valueOf(adjacentMines));
                button.setEnabled(false);
                button.setBackground(Color.WHITE);
            } else {
                button.setEnabled(false);
                button.setBackground(Color.WHITE);

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int newRow = row + i;
                        int newCol = col + j;

                        openCell(newRow, newCol);
                    }
                }
            }

            cell.setOpened(true);
            closedCells--;
            updateScore(1);
            checkVictory();
        }
    }

    private void gameOver() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j].isMine()) {
                    buttons[i][j].setText("M");
                    buttons[i][j].setEnabled(false);
                }
            }
        }

        JOptionPane.showMessageDialog(frame, "Game Over!", "Campo Minado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void checkVictory() {
        if (closedCells == numMines) {
            JOptionPane.showMessageDialog(frame, "Parabéns! Você venceu o jogo!", "Vitória", JOptionPane.INFORMATION_MESSAGE);
            restartGame();
        }
    }

    private void restartGame() {
        frame.dispose();
        new CampoMinado(boardSize, numMines);
    }

    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
    }

    private void updateScore(int points) {
        score += points;
        scoreLabel.setText("Score: " + score);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (button == buttons[i][j]) {
                    openCell(i, j);
                    return;
                }
            }
        }
    }

    private static class Cell {
        private boolean mine;
        private boolean opened;
        private int adjacentMines;

        public boolean isMine() {
            return mine;
        }

        public void setMine(boolean mine) {
            this.mine = mine;
        }

        public boolean isOpened() {
            return opened;
        }

        public void setOpened(boolean opened) {
            this.opened = opened;
        }

        public int getAdjacentMines() {
            return adjacentMines;
        }

        public void setAdjacentMines(int adjacentMines) {
            this.adjacentMines = adjacentMines;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CampoMinado(DEFAULT_SIZE, DEFAULT_MINES);
        });
    }
}
