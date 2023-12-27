import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;

/**
 * Class Main call methods to perform calculations.
 */
public final class Main {
    /**
     * Constructor for Main.
     */
    private Main() { }
    /**
     * private static Board chessBoard in Main class, where we read input from file input.txt and write to output.txt.
     */
    private static Board chessBoard;
    /**
     * Method main in class Main.
     * @param args
     * @throws IOException
     * @throws InvalidPiecePositionException
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        int numberOfBlackKing = 0;
        int numberOfWhiteKing = 0;
        final int nmin = 3;
        final int nmax = 1000;
        final int mmin = 2;
        final int el = 3;
        String line;
        File file = new File("input.txt");
        FileWriter fw = new FileWriter("output.txt");
        FileReader fr = new FileReader(file);
        BufferedReader reader = new BufferedReader(fr);
        int n = Integer.parseInt(reader.readLine());
        int m = Integer.parseInt(reader.readLine());
        line = reader.readLine();
        try {
            if (n < nmin || n > nmax) {
                throw new InvalidBoardSizeException();
            }
        } catch (InvalidBoardSizeException e) {
            fw.write(e.getMessage());
            fr.close();
            fw.close();
            System.exit(0);
        }
        Main.chessBoard = new Board(n);
        try {
            if (m < mmin || m > n * n) {
                throw new InvalidNumberOfPiecesException();
            }
        } catch (InvalidNumberOfPiecesException e) {
            fw.write(e.getMessage());
            fw.close();
            fr.close();
            System.exit(0);
        }
        int countM = 0;
        ChessPiece chessPiece = null;
        ArrayList<ChessPiece> arrayList = new ArrayList<>();
        while (line != null) {
            if (!line.isEmpty()) {
                String[] elements = line.split(" ");
                String piece = elements[0];
                try {
                    if (!(piece.equals("King") || piece.equals("Knight") || piece.equals("Pawn")
                            || piece.equals("Queen") || piece.equals("Bishop") || piece.equals("Rook"))) {
                        throw new InvalidPieceNameException();
                    }
                } catch (InvalidPieceNameException e) {
                    fw.write(e.getMessage());
                    fr.close();
                    fw.close();
                    System.exit(0);
                }
                String color = elements[1];
                int x = Integer.parseInt(elements[2]);
                int y = Integer.parseInt(elements[el]);
                PiecePosition piecePosition = new PiecePosition(x, y);
                PieceColor pieceColor = null;
                try {
                    pieceColor = PieceColor.parse(color);
                } catch (InvalidPieceColorException e) {
                    fw.write(e.getMessage());
                    fr.close();
                    fw.close();
                    System.exit(0);
                }
                try {
                    chessBoard.getPiece(piecePosition);
                } catch (InvalidPiecePositionException e) {
                    fw.write(e.getMessage());
                    fw.close();
                    fr.close();
                    System.exit(0);
                }
                try {
                    if (x < 1 || x > n || y < 1 || y > n) {
                        throw new InvalidPiecePositionException();
                    }
                } catch (InvalidPiecePositionException e) {
                    fw.write(e.getMessage());
                    fw.close();
                    fr.close();
                    System.exit(0);
                }
                if (piece.equals("Knight")) {
                    chessPiece = new Knight(piecePosition, pieceColor);
                } else if (piece.equals("King")) {
                    chessPiece = new King(piecePosition, pieceColor);
                    if (pieceColor == PieceColor.WHITE) {
                        numberOfWhiteKing++;
                    } else {
                        numberOfBlackKing++;
                    }
                } else if (piece.equals("Pawn")) {
                    chessPiece = new Pawn(piecePosition, pieceColor);
                } else if (piece.equals("Bishop")) {
                    chessPiece = new Bishop(piecePosition, pieceColor);
                } else if (piece.equals("Rook")) {
                    chessPiece = new Rook(piecePosition, pieceColor);
                } else if (piece.equals("Queen")) {
                    chessPiece = new Queen(piecePosition, pieceColor);
                }
                chessBoard.addPiece(chessPiece);
                arrayList.add(chessPiece);
                try {
                    if (numberOfBlackKing != 1 && numberOfWhiteKing != 1) {
                        throw new InvalidGivenKingsException();
                    } else if (numberOfBlackKing > 1 || numberOfWhiteKing > 1) {
                        throw new InvalidGivenKingsException();
                    }
                } catch (InvalidGivenKingsException e) {
                    fw.write(e.getMessage());
                    fw.close();
                    fr.close();
                    System.exit(0);
                }
                countM++;
            }
            line = reader.readLine();
        }
        try {
            if (countM < m || countM > m) {
                throw new InvalidNumberOfPiecesException();
            }
        } catch (InvalidNumberOfPiecesException e) {
            fw.write(e.getMessage());
            fw.close();
            fr.close();
            System.exit(0);
        }
        for (ChessPiece item : arrayList) {
            int f = chessBoard.getPiecesPossibleMovesCount(item);
            int s = chessBoard.getPiecesPossibleCapturesCount(item);
            fw.write(f + " " + s + "\n");
        }
        fw.close();
        fr.close();
    }
}

/**
 * enumeration PieceColor for determine color of Piece.
 */
enum PieceColor {
    /**
     * WHITE.
     */
    WHITE,
    /**
     * BLACK.
     */
    BLACK;
    // choose color of a figure
    public static PieceColor parse(String color) throws InvalidPieceColorException {
        if (color.equals("White")) {
            return WHITE;
        } else if (color.equals("Black")) {
            return BLACK;
        } else {
            throw new InvalidPieceColorException();
        }

    }
}

/**
 * class Board for description pieces on chess board.
 */
class Board {
    /**
     * Map for description chess pieces on board.
     */
    private Map<String, ChessPiece> positionsToPieces = new HashMap<>();
    /**
     * size of board.
     */
    private int size;

    Board(int boardSize) {
        this.size = boardSize;
    }

    /**
     * Method to get a number of Possible Moves of current piece.
     * @param piece
     * @return piece.getMovesCount
     */
    public int getPiecesPossibleMovesCount(ChessPiece piece) { // return get moves count
        return piece.getMovesCount(positionsToPieces, size);
    }

    /**
     * Method to get a number of Possible Captures of current piece.
     * @param piece
     * @return piece.getCapturesCount
     */
    public int getPiecesPossibleCapturesCount(ChessPiece piece) { // return get capture count
        return piece.getCapturesCount(positionsToPieces, size);
    }

    /**
     * Method void for add new piece in Map.
     * @param piece
     */
    public void addPiece(ChessPiece piece) { // add new piece into map
        PiecePosition piecePosition = piece.position;
        this.positionsToPieces.put(piecePosition.toString(), piece);
    }

    /**
     * Method for get piece from board via coordinates(PiecePosition).
     * @param position
     * @return this.positionsToPieces.get
     * @throws InvalidPiecePositionException
     */
    public ChessPiece getPiece(PiecePosition position) throws InvalidPiecePositionException {
        if (!positionsToPieces.containsKey(position.toString())) {
            return this.positionsToPieces.get(position.toString());
        } else {
            throw new InvalidPiecePositionException();
        }
    }
}

/**
 * class PiecePosition consists the coordinates of each chess pieces.
 */
class PiecePosition {
    /**
     * private int x is coordinate x.
     */
    private int x;
    /**
     * private int y is coordinate y.
     */
    private int y;

    PiecePosition(int onX, int onY) {

        this.x = onX;
        this.y = onY;
    }

    /**
     * getX for get coordinate x.
     * @return this.x
     */
    public int getX() { //return x
        return this.x;
    }

    /**
     * getY for get coordinate y.
     * @return this.y
     */
    public int getY() { //return y
        return this.y;
    }

    /**
     * Override Method toString for convert coordinates into Strign.
     * @return this.x + " " + this.y
     */
    @Override
    public String toString() {
        return this.x + " " + this.y;
    }
}

/**
 * interface for Bishop.
 */
interface BishopMovement {
    /**
     * getDiagonalMovesCount for pieces like Queen and Bishop.
     * @param position
     * @param color
     * @param positions
     * @param boardSize
     * @return res
     */
    int getDiagonalMovesCount(PiecePosition position, PieceColor color,
                              Map<String, ChessPiece> positions, int boardSize); // method getDiagonalMovesCount in interface
    /**
     * getDiagonalCapturesCount for pieces like Queen and Bishop.
     * @param position
     * @param color
     * @param positions
     * @param boardSize
     * @return res
     */
    int getDiagonalCapturesCount(PiecePosition position, PieceColor color,
                                 Map<String, ChessPiece> positions, int boardSize); // method getDiagonalCapturesCount in interface
}
/**
 * interface for Rook.
 */
interface RookMovement {
    /**
     * getOrthogonalMovesCount for pieces like Rook and Queen.
     * @param position
     * @param color
     * @param positions
     * @param boardSize
     * @return res
     */
    int getOrthogonalMovesCount(PiecePosition position, PieceColor color,
                                Map<String, ChessPiece> positions, int boardSize); // method getOrthogonalMovesCount in interface
    /**
     * getDiagonalCapturesCount for pieces like Queen and Bishop.
     * @param position
     * @param color
     * @param positions
     * @param boardSize
     * @return res
     */
    int getOrthogonalCapturesCount(PiecePosition position, PieceColor color,
                                   Map<String, ChessPiece> positions, int boardSize); // method getOrthogonalCapturesCount in interface
}

/**
 * abstract class ChessPiece contains characteristics about chess pieces.
 */
abstract class ChessPiece {
    /**
     * protected PiecePosition position is  position of piece in board now.
     */
    protected PiecePosition position;
    /**
     * protected PieceColor color is color of piece which we take.
     */
    protected PieceColor color;

    ChessPiece(PiecePosition piecePosition, PieceColor pieceColor) {
        this.position = piecePosition;
        this.color = pieceColor;
    }

    public abstract PiecePosition getPosition();

    public abstract PieceColor getColor();

    public abstract int getMovesCount(Map<String, ChessPiece> positions, int boardSize);

    public abstract int getCapturesCount(Map<String, ChessPiece> positions, int boardSize);
}

/**
 * class Knights extends ChessPiece contains description about Knight chess piece.
 */
class Knight extends ChessPiece {
    Knight(PiecePosition position, PieceColor color) {
        super(position, color);
    }

    /**
     * Override Method from ChessPiece class for get position.
     * @return super.position
     */
    @Override
    public PiecePosition getPosition() {
        return super.position;
    }
    /**
     * Override Method from ChessPiece class for get color.
     * @return super.color
     */
    @Override
    public PieceColor getColor() {
        return super.color;
    }

    /**
     * Method getCapturesCount for compute possible captures by Knight piece.
     * @param positions
     * @param boardSize
     * @return res
     */
    @Override
    public int getCapturesCount(Map<String, ChessPiece> positions, int boardSize) {
        int x = this.position.getX();
        int y = this.position.getY();
        int res = 0;
        if ((x + 2 <= boardSize) && (y + 1 <= boardSize) && (positions.containsKey((x + 2) + " " + (y + 1)))
                && (this.color != positions.get((x + 2) + " " + (y + 1)).getColor())) {
            res++;
        }
        if ((x + 1 <= boardSize) && (y + 2 <= boardSize) && (positions.containsKey((x + 1) + " " + (y + 2)))
                && (this.color != positions.get((x + 1) + " " + (y + 2)).getColor())) {
            res++;
        }
        if ((x + 2 <= boardSize) && (y - 1 >= 1) && (positions.containsKey((x + 2) + " " + (y - 1)))
                && (this.color != positions.get((x + 2) + " " + (y - 1)).getColor())) {
            res++;
        }
        if ((x + 1 <= boardSize) && (y - 2 >= 1) && (positions.containsKey((x + 1) + " " + (y - 2)))
                && (this.color != positions.get((x + 1) + " " + (y - 2)).getColor())) {
            res++;
        }
        if ((x - 2 >= 1) && (y + 1 <= boardSize) && (positions.containsKey((x - 2) + " " + (y + 1)))
                && (this.color != positions.get((x - 2) + " " + (y + 1)).getColor())) {
            res++;
        }
        if ((x - 1 >= 1) && (y + 2 <= boardSize) && (positions.containsKey((x - 1) + " " + (y + 2)))
                && (this.color != positions.get((x - 1) + " " + (y + 2)).getColor())) {
            res++;
        }
        if ((x - 2 >= 1) && (y - 1 >= 1) && (positions.containsKey((x - 2) + " " + (y - 1)))
                && (this.color != positions.get((x - 2) + " " + (y - 1)).getColor())) {
            res++;
        }
        if ((x - 1 >= 1) && (y - 2 >= 1) && (positions.containsKey((x - 1) + " " + (y - 2)))
                && (this.color != positions.get((x - 1) + " " + (y - 2)).getColor())) {
            res++;
        }
        return res;
    }

    /**
     * Method for get possible moves count by Knight piece.
     * @param positions
     * @param boardSize
     * @return res
     */
    @Override
    public int getMovesCount(Map<String, ChessPiece> positions, int boardSize) { // method getMovesCount
        int x = this.position.getX();
        int y = this.position.getY();
        int res = 0;
        if ((x + 2 <= boardSize) && (y + 1 <= boardSize)) {
            if ((positions.containsKey((x + 2) + " " + (y + 1)))
                    && (this.color != positions.get((x + 2) + " " + (y + 1)).getColor())) {
                res++;
            } else if (!positions.containsKey((x + 2) + " " + (y + 1))) {
                res++;
            }
        }
        if ((x + 1 <= boardSize) && (y + 2 <= boardSize)) {
            if ((positions.containsKey((x + 1) + " " + (y + 2)))
                    && (this.color != positions.get((x + 1) + " " + (y + 2)).getColor())) {
                res++;
            } else if (!positions.containsKey((x + 1) + " " + (y + 2))) {
                res++;
            }
        }
        if ((x + 2 <= boardSize) && (y - 1 >= 1)) {
            if ((positions.containsKey((x + 2) + " " + (y - 1)))
                    && (this.color != positions.get((x + 2) + " " + (y - 1)).getColor())) {
                res++;
            } else if (!positions.containsKey((x + 2) + " " + (y - 1))) {
                res++;
            }
        }
        if ((x + 1 <= boardSize) && (y - 2 >= 1)) {
            if ((positions.containsKey((x + 1) + " " + (y - 2)))
                    && (this.color != positions.get((x + 1) + " " + (y - 2)).getColor())) {
                res++;
            } else if (!positions.containsKey((x + 1) + " " + (y - 2))) {
                res++;
            }
        }
        if ((x - 2 >= 1) && (y + 1 <= boardSize)) {
            if ((positions.containsKey((x - 2) + " " + (y + 1)))
                    && (this.color != positions.get((x - 2) + " " + (y + 1)).getColor())) {
                res++;
            } else if (!positions.containsKey((x - 2) + " " + (y + 1))) {
                res++;
            }
        }
        if ((x - 1 >= 1) && (y + 2 <= boardSize)) {
            if ((positions.containsKey((x - 1) + " " + (y + 2)))
                    && (this.color != positions.get((x - 1) + " " + (y + 2)).getColor())) {
                res++;
            } else if (!positions.containsKey((x - 1) + " " + (y + 2))) {
                res++;
            }
        }
        if ((x - 2 >= 1) && (y - 1 >= 1)) {
            if ((positions.containsKey((x - 2) + " " + (y - 1)))
                    && (this.color != positions.get((x - 2) + " " + (y - 1)).getColor())) {
                res++;
            } else if (!positions.containsKey((x - 2) + " " + (y - 1))) {
                res++;
            }
        }
        if ((x - 1 >= 1) && (y - 2 >= 1)) {
            if ((positions.containsKey((x - 1) + " " + (y - 2)))
                    && (this.color != positions.get((x - 1) + " " + (y - 2)).getColor())) {
                res++;
            } else if (!positions.containsKey((x - 1) + " " + (y - 2))) {
                res++;
            }
        }
        return res;
    }
}

/**
 * class King extends ChessPiece contains characteristic about King chess piece.
 */
class King extends ChessPiece {
    King(PiecePosition position, PieceColor color) {
        super(position, color);
    }

    /**
     * Override method for get position by King piece.
     * @return super.position
     */
    @Override
    public PiecePosition getPosition() { // method getPosition
        return super.position;
    }

    /**
     * Override method for get color by King piece.
     * @return super.color
     */
    @Override
    public PieceColor getColor() { // method getColor
        return super.color;
    }

    /**
     * Method for get possible moves count by King piece.
     * @param positions
     * @param boardSize
     * @return res
     */
    @Override
    public int getMovesCount(Map<String, ChessPiece> positions, int boardSize) { // method getMovesCount
        int x = this.position.getX();
        int y = this.position.getY();
        int res = 0;
        if ((x + 1 <= boardSize) && (y + 1 <= boardSize)) {
            if ((positions.containsKey((x + 1) + " " + (y + 1)))
                    && (this.color != positions.get((x + 1) + " " + (y + 1)).getColor())) {
                res++;
            } else if (!positions.containsKey((x + 1) + " " + (y + 1))) {
                res++;
            }
        }
        if (x + 1 <= boardSize) {
            if ((positions.containsKey((x + 1) + " " + y)
                    && (this.color != positions.get((x + 1) + " " + y).getColor()))) {
                res++;
            } else if (!positions.containsKey((x + 1) + " " + y)) {
                res++;
            }
        }
        if ((x + 1 <= boardSize) && (y - 1 >= 1)) {
            if ((positions.containsKey((x + 1) + " " + (y - 1)))
                    && (this.color != positions.get((x + 1) + " " + (y - 1)).getColor())) {
                res++;
            } else if (!positions.containsKey((x + 1) + " " + (y - 1))) {
                res++;
            }
        }
        if ((y - 1 >= 1)) {
            if ((positions.containsKey(x + " " + (y - 1)))
                    && (this.color != positions.get(x + " " + (y - 1)).getColor())) {
                res++;
            } else if (!positions.containsKey(x + " " + (y - 1))) {
                res++;
            }
        }
        if ((x - 1 >= 1) && (y - 1 >= 1)) {
            if ((positions.containsKey((x - 1) + " " + (y - 1)))
                    && (this.color != positions.get((x - 1) + " " + (y - 1)).getColor())) {
                res++;
            } else if (!positions.containsKey((x - 1) + " " + (y - 1))) {
                res++;
            }
        }
        if ((x - 1 >= 1)) {
            if ((positions.containsKey((x - 1) + " " + y))
                    && (this.color != positions.get((x - 1) + " " + y).getColor())) {
                res++;
            } else if (!positions.containsKey((x - 1) + " " + y)) {
                res++;
            }
        }
        if ((x - 1 >= 1) && (y + 1 <= boardSize)) {
            if ((positions.containsKey((x - 1) + " " + (y + 1)))
                    && (this.color != positions.get((x - 1) + " " + (y + 1)).getColor())) {
                res++;
            } else if (!positions.containsKey((x - 1) + " " + (y + 1))) {
                res++;
            }
        }
        if (y + 1 <= boardSize) {
            if ((positions.containsKey(x + " " + (y + 1)))
                    && (this.color != positions.get(x + " " + (y + 1)).getColor())) {
                res++;
            } else if (!positions.containsKey(x + " " + (y + 1))) {
                res++;
            }
        }
        return res;
    }

    /**
     * Method for get possible captures count by King piece.
     * @param positions
     * @param boardSize
     * @return res
     */
    @Override
    public int getCapturesCount(Map<String, ChessPiece> positions, int boardSize) { // method getCapturesCount
        int x = this.position.getX();
        int y = this.position.getY();
        int res = 0;
        if ((x + 1 <= boardSize) && (y + 1 <= boardSize)) {
            if ((positions.containsKey((x + 1) + " " + (y + 1)))
                    && (this.color != positions.get((x + 1) + " " + (y + 1)).getColor())) {
                res++;
            }
        }
        if (x + 1 <= boardSize) {
            if ((positions.containsKey((x + 1) + " " + y)
                    && (this.color != positions.get((x + 1) + " " + y).getColor()))) {
                res++;
            }
        }
        if ((x + 1 <= boardSize) && (y - 1 >= 1)) {
            if ((positions.containsKey((x + 1) + " " + (y - 1)))
                    && (this.color != positions.get((x + 1) + " " + (y - 1)).getColor())) {
                res++;
            }
        }
        if ((y - 1 >= 1)) {
            if ((positions.containsKey(x + " " + (y - 1)))
                    && (this.color != positions.get((x) + " " + (y - 1)).getColor())) {
                res++;
            }
        }
        if ((x - 1 >= 1) && (y - 1 >= 1)) {
            if ((positions.containsKey((x - 1) + " " + (y - 1)))
                    && (this.color != positions.get((x - 1) + " " + (y - 1)).getColor())) {
                res++;
            }
        }
        if ((x - 1 >= 1)) {
            if ((positions.containsKey((x - 1) + " " + y))
                    && (this.color != positions.get((x - 1) + " " + y).getColor())) {
                res++;
            }
        }
        if ((x - 1 >= 1) && (y + 1 <= boardSize)) {
            if ((positions.containsKey((x - 1) + " " + (y + 1)))
                    && (this.color != positions.get((x - 1) + " " + (y + 1)).getColor())) {
                res++;
            }
        }
        if (y + 1 <= boardSize) {
            if ((positions.containsKey(x + " " + (y + 1)))
                    && (this.color != positions.get(x + " " + (y + 1)).getColor())) {
                res++;
            }
        }
        return res;
    }
}

/**
 * class Pawn extends contains characteristic about Pawn chess piece.
 */
class Pawn extends ChessPiece {
    Pawn(PiecePosition position, PieceColor color) {
        super(position, color);
    }

    /**
     * Override method for get position by Pawn piece.
     * @return super.position
     */
    @Override
    public PiecePosition getPosition() { // method getPosition
        return super.position;
    }

    /**
     * Override method for get color by Pawn piece.
     * @return super.color
     */
    @Override
    public PieceColor getColor() { // method getColor
        return super.color;
    }

    /**
     * Method for compute possible moves count by Pawn piece.
     * @param positions
     * @param boardSize
     * @return res
     */
    @Override
    public int getMovesCount(Map<String, ChessPiece> positions, int boardSize) { // method getMovesCount
        int x = this.position.getX();
        int y = this.position.getY();
        int res = 0;
        switch (getColor()) {
            case WHITE:
                if ((y + 1 <= boardSize) && (!positions.containsKey(x + " " + (y + 1)))) {
                    res++;
                }
                break;
            case BLACK:
                if ((y - 1 >= 1) && (!positions.containsKey(x + " " + (y - 1)))) {
                    res++;
                }
                break;
            default:
                break;
        }
        return res + getCapturesCount(positions, boardSize);
    }

    /**
     * Method for compute possible captures count by Pawn piece.
     * @param positions
     * @param boardSize
     * @return res
     */
    public int getCapturesCount(Map<String, ChessPiece> positions, int boardSize) { // method getCapturesCount
        int x = this.position.getX();
        int y = this.position.getY();
        int res = 0;
        switch (getColor()) {
            case WHITE:
                if ((x + 1 <= boardSize) && (y + 1 <= boardSize) && (positions.containsKey((x + 1) + " " + (y + 1)))
                        && (this.color != positions.get((x + 1) + " " + (y + 1)).getColor())) {
                    res++;
                }
                if ((x - 1 >= 1) && (y + 1 <= boardSize) && (positions.containsKey((x - 1) + " " + (y + 1)))
                        && (this.color != positions.get((x - 1) + " " + (y + 1)).getColor())) {
                    res++;
                }
                break;
            case BLACK:
                if ((x - 1 >= 1) && (y - 1 >= 1) && (positions.containsKey((x - 1) + " " + (y - 1)))
                        && (this.color != positions.get((x - 1) + " " + (y - 1)).getColor())) {
                    res++;
                }
                if ((x + 1 <= boardSize) && (y - 1 >= 1) && (positions.containsKey((x + 1) + " " + (y - 1)))
                        && (this.color != positions.get((x + 1) + " " + (y - 1)).getColor())) {
                    res++;
                }
                break;
            default:
                break;
        }
        return res;
    }
}

/**
 * class Bishop extends ChessPiece implements BishopMovement contains characteristic about Bishop chess piece.
 */
class Bishop extends ChessPiece implements BishopMovement {
    Bishop(PiecePosition position, PieceColor color) {
        super(position, color);
    }

    /**
     * Mehtod for get position by Bishop piece.
     * @return super.position
     */
    @Override
    public PiecePosition getPosition() { // method getPosition
        return super.position;
    }

    /**
     * Method for get color by Bishop piece.
     * @return super.color
     */
    @Override
    public PieceColor getColor() { // method getColor
        return super.color;
    }

    /**
     * Method for get possible moves count by Bishop piece.
     * @param positions
     * @param boardSize
     * @return getDiagonalMovesCount
     */
    @Override
    public int getMovesCount(Map<String, ChessPiece> positions, int boardSize) { // method getMovesCount
        return getDiagonalMovesCount(this.position, this.color, positions, boardSize);
    }

    /**
     * Method for get possible captures by Bishop piece.
     * @param positions
     * @param boardSize
     * @returnget DiagonalCapturesCount
     */
    @Override
    public int getCapturesCount(Map<String, ChessPiece> positions, int boardSize) { // method getCapturesCount
        return getDiagonalCapturesCount(this.position, this.color, positions, boardSize);
    }

    /**
     * Method for calculate possible diagonal moves by Bishop piece.
     * @param position
     * @param color
     * @param positions
     * @param boardSize
     * @return res
     */
    @Override
    public int getDiagonalMovesCount(PiecePosition position, PieceColor color,
                                     Map<String, ChessPiece> positions, int boardSize) { // method getDiagonalMovesCount
        int x = position.getX();
        int y = position.getY();
        boolean flag1 = true;
        boolean flag2 = true;
        boolean flag3 = true;
        boolean flag4 = true;
        int dif = 1;
        int res = 0;
        while (flag1 || flag2 || flag3 || flag4) {
            if ((flag1) && (x + dif <= boardSize) && (y + dif <= boardSize)
                    && (!positions.containsKey((x + dif) + " " + (y + dif)))) {
                res++;
            } else if ((flag1) && (x + dif <= boardSize) && (y + dif <= boardSize)
                    && (positions.containsKey((x + dif) + " " + (y + dif)))) {
                if (this.color != positions.get((x + dif) + " " + (y + dif)).getColor()) {
                    res++;
                }
                flag1 = false;
            } else {
                flag1 = false;
            }
            if ((flag2) && (x - dif >= 1) && (y + dif <= boardSize)
                    && (!positions.containsKey((x - dif) + " " + (y + dif)))) {
                res++;
            } else if (((flag2) && (x - dif >= 1) && (y + dif <= boardSize)
                    && (positions.containsKey((x - dif) + " " + (y + dif))))) {
                if (this.color != positions.get((x - dif) + " " + (y + dif)).getColor()) {
                    res++;
                }
                flag2 = false;
            } else {
                flag2 = false;
            }
            if ((flag3) && (x - dif >= 1) && (y - dif >= 1)
                    && (!positions.containsKey((x - dif) + " " + (y - dif)))) {
                res++;
            } else if (((flag3) && (x - dif >= 1) && (y - dif >= 1)
                    && (positions.containsKey((x - dif) + " " + (y - dif))))) {
                if (this.color != positions.get((x - dif) + " " + (y - dif)).getColor()) {
                    res++;
                }
                flag3 = false;
            } else {
                flag3 = false;
            }
            if ((flag4) && (x + dif <= boardSize) && (y - dif >= 1)
                    && (!positions.containsKey((x + dif) + " " + (y - dif)))) {
                res++;
            } else if ((flag4) && (x + dif <= boardSize) && (y - dif >= 1)
                    && (positions.containsKey((x + dif) + " " + (y - dif)))) {
                if (this.color != positions.get((x + dif) + " " + (y - dif)).getColor()) {
                    res++;
                }
                flag4 = false;
            } else {
                flag4 = false;
            }
            dif++;
        }
        return res;
    }

    /**
     * Method for calculate diagonal captures count by Bishop piece.
     * @param position
     * @param color
     * @param positions
     * @param boardSize
     * @return res
     */
    @Override
    public int getDiagonalCapturesCount(PiecePosition position, PieceColor color,
                                        Map<String, ChessPiece> positions, int boardSize) { // method getDiagonalCapturesCount
        int x = position.getX();
        int y = position.getY();
        boolean flag1 = true;
        boolean flag2 = true;
        boolean flag3 = true;
        boolean flag4 = true;
        int dif = 1;
        int res = 0;
        while (flag1 || flag2 || flag3 || flag4) {
            String key1 = (x + dif) + " " + (y + dif);
            if ((flag1) && (x + dif <= boardSize) && (y + dif <= boardSize) && (positions.containsKey(key1))) {
                if (this.color != positions.get(key1).getColor()) {
                    res++;
                }
                flag1 = false;
            } else if ((x + dif > boardSize) || (y + dif > boardSize)) {
                flag1 = false;
            }
            String key2 = (x - dif) + " " + (y + dif);
            if ((flag2) && (x - dif >= 1) && (y + dif <= boardSize) && (positions.containsKey(key2))) {
                if (this.color != positions.get(key2).getColor()) {
                    res++;
                }
                flag2 = false;
            } else if ((x - dif < 1) || (y + dif > boardSize)) {
                flag2 = false;
            }
            if ((flag3) && (x - dif >= 1) && (y - dif >= 1) && (positions.containsKey((x - dif) + " " + (y - dif)))) {
                if (this.color != positions.get((x - dif) + " " + (y - dif)).getColor()) {
                    res++;
                }
                flag3 = false;
            } else if ((x - dif < 1) || (y - dif < 1)) {
                flag3 = false;
            }
            if ((flag4) && (x + dif <= boardSize) && (y - dif >= 1)
                    && (positions.containsKey((x + dif) + " " + (y - dif)))) {
                if (this.color != positions.get((x + dif) + " " + (y - dif)).getColor()) {
                    res++;
                }
                flag4 = false;
            } else if ((x + dif > boardSize) || (y - dif < 1)) {
                flag4 = false;
            }
            dif++;
        }
        return res;
    }
}

/**
 * class Rook extends ChessPiece implements RookMovement contains characteristic about Rook chess piece.
 */
class Rook extends ChessPiece implements RookMovement {
    Rook(PiecePosition position, PieceColor color) {
        super(position, color);
    }
    /**
     * Override method for get position by Rook piece.
     * @return super.position
     */
    @Override
    public PiecePosition getPosition() { // method get position
        return super.position;
    }

    /**
     * Override method for get color by Rook piece.
     * @return super.color
     */
    @Override
    public PieceColor getColor() { // method get color
        return super.color;
    }

    /**
     * Method for get possible moves count by Rook piece.
     * @param positions
     * @param boardSize
     * @return getOrthogonalMovesCount
     */
    @Override
    public int getMovesCount(Map<String, ChessPiece> positions, int boardSize) { // method getMovesCount
        return getOrthogonalMovesCount(this.position, this.color, positions, boardSize);
    }

    /**
     * Method for get possible captures count by Rook piece.
     * @param positions
     * @param boardSize
     * @return getOrthogonalCapturesCount
     */
    @Override
    public int getCapturesCount(Map<String, ChessPiece> positions, int boardSize) { // method getCapturesCount
        return getOrthogonalCapturesCount(this.position, this.color, positions, boardSize);
    }

    /**
     * Method for calculate orthogonal moves count by Rook piece.
     * @param position
     * @param color
     * @param positions
     * @param boardSize
     * @return res
     */
    @Override
    public int getOrthogonalMovesCount(PiecePosition position, PieceColor color,
                                       Map<String, ChessPiece> positions, int boardSize) { // method getOrthogonalMovesCount
        int x = position.getX();
        int y = position.getY();
        boolean flag1 = true;
        boolean flag2 = true;
        boolean flag3 = true;
        boolean flag4 = true;
        int dif = 1;
        int res = 0;
        while (flag1 || flag2 || flag3 || flag4) {
            if (flag1 && (x + dif <= boardSize) && !positions.containsKey((x + dif) + " " + y)) {
                res++;
            } else if (flag1 && (x + dif <= boardSize) && positions.containsKey((x + dif) + " " + y)) {
                if (this.color != positions.get((x + dif) + " " + y).getColor()) {
                    res++;
                }
                flag1 = false;
            } else {
                flag1 = false;
            }
            if (flag2 && (x - dif >= 1) && !positions.containsKey((x - dif) + " " + y)) {
                res++;
            } else if (flag2 && (x - dif >= 1) && positions.containsKey((x - dif) + " " + y)) {
                if (this.color != positions.get((x - dif) + " " + y).getColor()) {
                    res++;
                }
                flag2 = false;
            } else {
                flag2 = false;
            }
            if (flag3 && (y + dif <= boardSize) && !positions.containsKey((x + " " + (y + dif)))) {
                res++;
            } else if (flag3 && (y + dif <= boardSize) && positions.containsKey((x + " " + (y + dif)))) {
                if (this.color != positions.get(x + " " + (y + dif)).getColor()) {
                    res++;
                }
                flag3 = false;
            } else {
                flag3 = false;
            }
            if (flag4 && (y - dif >= 1) && !positions.containsKey(x + " " + (y - dif))) {
                res++;
            } else if (flag4 && (y - dif >= 1) && positions.containsKey(x + " " + (y - dif))) {
                if (this.color != positions.get(x + " " + (y - dif)).getColor()) {
                    res++;
                }
                flag4 = false;
            } else {
                flag4 = false;
            }
            dif++;
        }
        return res;
    }

    /**
     * Method for calculate possible orthogonal captures count by Rook piece.
     * @param position
     * @param color
     * @param positions
     * @param boardSize
     * @return res
     */
    @Override
    public int getOrthogonalCapturesCount(PiecePosition position, PieceColor color,
                                          Map<String, ChessPiece> positions, int boardSize) {
        int x = position.getX();
        int y = position.getY();
        boolean flag1 = true;
        boolean flag2 = true;
        boolean flag3 = true;
        boolean flag4 = true;
        int dif = 1;
        int res = 0;
        while (flag1 || flag2 || flag3 || flag4) {
            if (flag1 && (x + dif <= boardSize) && positions.containsKey((x + dif) + " " + y)) {
                if (this.color != positions.get((x + dif) + " " + y).getColor()) {
                    res++;
                }
                flag1 = false;
            } else if (x + dif > boardSize) {
                flag1 = false;
            }
            if (flag2 && (x - dif >= 1) && positions.containsKey((x - dif) + " " + y)) {
                if (this.color != positions.get((x - dif) + " " + y).getColor()) {
                    res++;
                }
                flag2 = false;
            } else if (x - dif < 1) {
                flag2 = false;
            }
            if (flag3 && (y + dif <= boardSize) && positions.containsKey((x + " " + (y + dif)))) {
                if (this.color != positions.get(x + " " + (y + dif)).getColor()) {
                    res++;
                }
                flag3 = false;
            } else if (y + dif > boardSize) {
                flag3 = false;
            }
            if (flag4 && (y - dif >= 1) && positions.containsKey(x + " " + (y - dif))) {
                if (this.color != positions.get(x + " " + (y - dif)).getColor()) {
                    res++;
                }
                flag4 = false;
            } else if (y - dif < 1) {
                flag4 = false;
            }
            dif++;
        }
        return res;
    }
}

/**
 * class Queen extends ChessPiece implements BishopMovement, RookMovement contains characteristics about Queen piece.
 */
class Queen extends ChessPiece implements BishopMovement, RookMovement {
    Queen(PiecePosition position, PieceColor color) {
        super(position, color);
    }
    /**
     * Override method for get position by Queen piece.
     * @return super.position
     */
    @Override
    public PiecePosition getPosition() {
        return super.position;
    }

    /**
     * Override method for get color by Queen piece.
     * @return super.color
     */
    @Override
    public PieceColor getColor() {
        return super.color;
    }

    /**
     * Method for get possible moves count by Queen piece.
     * @param positions
     * @param boardSize
     * @return this.getDiagonalMovesCount + this.getOrthogonalMovesCount
     */
    @Override
    public int getMovesCount(Map<String, ChessPiece> positions, int boardSize) {
        return this.getDiagonalMovesCount(this.position, this.color, positions, boardSize)
                + this.getOrthogonalMovesCount(this.position, this.color, positions, boardSize);
    }

    /**
     * Method for get possible captures count by Queen piece.
     * @param positions
     * @param boardSize
     * @return getDiagonalCapturesCount + getOrthogonalCapturesCount
     */
    @Override
    public int getCapturesCount(Map<String, ChessPiece> positions, int boardSize) {
        return this.getDiagonalCapturesCount(this.position, this.color, positions, boardSize)
                + this.getOrthogonalCapturesCount(this.position, this.color, positions, boardSize);
    }
    /**
     * Method for calculate possible diagonal moves count by Queen piece.
     * @param position
     * @param color
     * @param positions
     * @param boardSize
     * @return chessPiece.getMovesCount
     */
    @Override
    public int getDiagonalMovesCount(PiecePosition position, PieceColor color,
                                     Map<String, ChessPiece> positions, int boardSize) {
        ChessPiece chessPiece = new Bishop(position, color);
        return chessPiece.getMovesCount(positions, boardSize);
    }

    /**
     * Method for calculate diagonal captures count by Queen piece.
     * @param position
     * @param color
     * @param positions
     * @param boardSize
     * @return chessPiece.getCapturesCount
     */
    @Override
    public int getDiagonalCapturesCount(PiecePosition position, PieceColor color,
                                        Map<String, ChessPiece> positions, int boardSize) {
        ChessPiece chessPiece = new Bishop(position, color);
        return chessPiece.getCapturesCount(positions, boardSize);
    }

    /**
     * Method for calculate orthogonal moves count by Queen piece.
     * @param position
     * @param color
     * @param positions
     * @param boardSize
     * @return chessPiece.getMovesCount
     */
    @Override
    public int getOrthogonalMovesCount(PiecePosition position, PieceColor color,
                                       Map<String, ChessPiece> positions, int boardSize) {
        ChessPiece chessPiece = new Rook(position, color);
        return chessPiece.getMovesCount(positions, boardSize);
    }

    /**
     * Method for calculate orthogonal captures count by Queen piece.
     * @param position
     * @param color
     * @param positions
     * @param boardSize
     * @return chessPiece.getCapturesCount
     */
    @Override
    public int getOrthogonalCapturesCount(PiecePosition position, PieceColor color,
                                          Map<String, ChessPiece> positions, int boardSize) {
        ChessPiece chessPiece = new Rook(position, color);
        return chessPiece.getCapturesCount(positions, boardSize);
    }
}

/**
 * class InvalidBoardSizeException extends Exception for kind of invalid inputs.
 */
class InvalidBoardSizeException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid board size";
    }
}

/**
 * class InvalidNumberOfPiecesException extends Exception for kind of invalid inputs.
 */
class InvalidNumberOfPiecesException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid number of pieces";
    }
}

/**
 * class InvalidPieceNameException extends Exception for kind of invalid inputs.
 */
class InvalidPieceNameException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid piece name";
    }
}

/**
 * class InvalidPieceColorException extends Exception for kind of invalid inputs.
 */
class InvalidPieceColorException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid piece color";
    }
}

/**
 * class InvalidPiecePositionException extends Exception for kind of invalid inputs.
 */
class InvalidPiecePositionException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid piece position";
    }
}

/**
 * class InvalidGivenKingsException extends Exception for kind of invalid inputs.
 */
class InvalidGivenKingsException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid given Kings";
    }
}

/**
 * class InvalidInputException extends Exception for kind of invalid inputs.
 */
class InvalidInputException extends Exception {
    @Override
    public String getMessage() {
        return "Invalid input";
    }
}
