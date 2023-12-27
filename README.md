# Description:
## Input format:
The input file (input.txt) consists of N (3≤N≤1000), the chess board size, followed on the next line by M (2≤M≤N2 ), the number of pieces on the board. Next M lines should follow the next format: PieceTypei Colori Xi Yi
where:
- PieceTypei represents the type of the i'th piece and should be a string from the following set: {"Pawn", "King", "Knight", "Rook", "Queen", "Bishop"}
- Colori represents if the i'th piece is colored white or black and should be from the following set {"White", "Black"}
- Xi represents the horizontal position of the i'th piece on the board and should be a positive integer number in the range [1,N]
- Yi represents the Vertical position of the i'th piece on the board and should be a positive integer number in the range [1,N]

## Output format:
The output file (output.txt) should consist of the M lines in the following format (unless there is an error) corresponding to the piece entered in the same order given in the input (first line represents the first piece entered, second line represents the second piece entered and so on)
Pi Ki where:
- Pi is the number of possible moves the i'th piece can make from the same cell supposing that it's the turn of the player who owns this piece (including captures). Consider that your moves are independent, so you technically have moves in parallel
- Ki is the number of captures the i'th piece can potentially do from the same cell. Consider that your captures are independent, so you technically have captures in paralle
