package sonemc.minesweeper.game;

import java.util.Random;

public class MinesweeperGame {

    private static final int ROWS = 6;
    private static final int COLS = 9;
    private static final int TOTAL_MINES = 12;

    private boolean[][] mines;
    private boolean[][] revealed;
    private boolean[][] flagged;
    private int[][] adjacentMines;
    private boolean gameOver;
    private boolean gameWon;
    private int revealedCount;
    private int flaggedCount;
    private boolean firstClick;

    public MinesweeperGame() {
        initializeGame();
    }

    private void initializeGame() {
        mines = new boolean[ROWS][COLS];
        revealed = new boolean[ROWS][COLS];
        flagged = new boolean[ROWS][COLS];
        adjacentMines = new int[ROWS][COLS];
        gameOver = false;
        gameWon = false;
        revealedCount = 0;
        flaggedCount = 0;
        firstClick = true;
    }

    private void placeMines(int safeRow, int safeCol) {
        Random random = new Random();
        int placedMines = 0;

        while (placedMines < TOTAL_MINES) {
            int row = random.nextInt(ROWS);
            int col = random.nextInt(COLS);

            if (isInSafeZone(row, col, safeRow, safeCol)) {
                continue;
            }

            if (!mines[row][col]) {
                mines[row][col] = true;
                placedMines++;
            }
        }
    }

    private boolean isInSafeZone(int row, int col, int safeRow, int safeCol) {
        return Math.abs(row - safeRow) <= 1 && Math.abs(col - safeCol) <= 1;
    }

    private void calculateAdjacentMines() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (!mines[row][col]) {
                    adjacentMines[row][col] = countAdjacentMines(row, col);
                }
            }
        }
    }

    private int countAdjacentMines(int row, int col) {
        int count = 0;

        for (int r = Math.max(0, row - 1); r <= Math.min(ROWS - 1, row + 1); r++) {
            for (int c = Math.max(0, col - 1); c <= Math.min(COLS - 1, col + 1); c++) {
                if (r != row || c != col) {
                    if (mines[r][c]) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    public boolean revealTile(int row, int col) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS || revealed[row][col] || flagged[row][col] || gameOver) {
            return false;
        }

        if (firstClick) {
            placeMines(row, col);
            calculateAdjacentMines();
            firstClick = false;

            revealSafeZone(row, col);
            return false;
        }

        revealed[row][col] = true;
        revealedCount++;

        if (mines[row][col]) {
            gameOver = true;
            return true;
        }

        if (adjacentMines[row][col] == 0) {
            revealAdjacentTiles(row, col);
        }

        checkWinCondition();

        return false;
    }

    public boolean toggleFlag(int row, int col) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS || revealed[row][col] || gameOver) {
            return false;
        }

        if (firstClick) {
            return false;
        }

        flagged[row][col] = !flagged[row][col];

        if (flagged[row][col]) {
            flaggedCount++;
        } else {
            flaggedCount--;
        }

        return true;
    }

    private void revealSafeZone(int centerRow, int centerCol) {
        for (int r = Math.max(0, centerRow - 1); r <= Math.min(ROWS - 1, centerRow + 1); r++) {
            for (int c = Math.max(0, centerCol - 1); c <= Math.min(COLS - 1, centerCol + 1); c++) {
                if (!revealed[r][c] && !mines[r][c]) {
                    revealed[r][c] = true;
                    revealedCount++;

                    if (adjacentMines[r][c] == 0) {
                        revealAdjacentTiles(r, c);
                    }
                }
            }
        }
    }

    private void revealAdjacentTiles(int row, int col) {
        for (int r = Math.max(0, row - 1); r <= Math.min(ROWS - 1, row + 1); r++) {
            for (int c = Math.max(0, col - 1); c <= Math.min(COLS - 1, col + 1); c++) {
                if (r != row || c != col) {
                    if (!revealed[r][c] && !mines[r][c] && !flagged[r][c]) {
                        revealTile(r, c);
                    }
                }
            }
        }
    }

    private void checkWinCondition() {
        int totalSafeTiles = ROWS * COLS - TOTAL_MINES;
        if (revealedCount >= totalSafeTiles) {
            gameWon = true;
            gameOver = true;
        }
    }

    public void revealAllMines() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (mines[row][col]) {
                    revealed[row][col] = true;
                }
            }
        }
    }

    public boolean isMine(int row, int col) {
        return mines[row][col];
    }

    public boolean isRevealed(int row, int col) {
        return revealed[row][col];
    }

    public boolean isFlagged(int row, int col) {
        return flagged[row][col];
    }

    public int getAdjacentMines(int row, int col) {
        return adjacentMines[row][col];
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isWon() {
        return gameWon;
    }

    public int getRevealedCount() {
        return revealedCount;
    }

    public int getFlaggedCount() {
        return flaggedCount;
    }

    public int getTotalMines() {
        return TOTAL_MINES;
    }

    public int getRemainingMines() {
        return TOTAL_MINES - flaggedCount;
    }

    public boolean isFirstClick() {
        return firstClick;
    }
}