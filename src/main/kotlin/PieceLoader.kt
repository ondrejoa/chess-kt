import javafx.scene.image.Image
import proto.ChessColor
import proto.ChessPiece

class PieceLoader {
    private val images: MutableMap<String, Image> = mutableMapOf()

    fun getImage(piece: ChessPiece): Image {
        val imagePath: String = "pieces/" + (if (piece.color == ChessColor.WHITE) "w_" else "b_") + piece.type.toString().lowercase() + ".png"
        var image = images[imagePath]
        return if (image != null) {
            image
        } else {
            image = Image(ChessApplication::class.java.getResource(imagePath)!!.toString())
            images[imagePath] = image
            image
        }
    }
}