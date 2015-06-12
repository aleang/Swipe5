package swipe5;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import static java.lang.System.out;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class DropFileListener implements DropTargetListener {
	private JLabel label;
	private SwipePanel panelPlay;
	
    public DropFileListener(JLabel l, SwipePanel p) {
    	this.label = l;
    	this.panelPlay = p;
	}
    
    public void drop(DropTargetDropEvent event) {
        // Accept copy drops
        event.acceptDrop(DnDConstants.ACTION_COPY);

        // Get the transfer which can provide the dropped item data
        Transferable transferable = event.getTransferable();

        // Get the data formats of the dropped item
        DataFlavor[] flavors = transferable.getTransferDataFlavors();

        // Loop through the flavors
        lookingThroughFlavours : for (DataFlavor flavour : flavors) {
            //out.println(flavour.getMimeType());
        	try {
                // If the drop items are text files; MIME type "text"
                if (flavour.isFlavorJavaFileListType()) {
                	
                    // Get all of the dropped files
                    List files = (List) transferable.getTransferData(flavour);
                    File file = (File) files.get(0);
                    panelPlay.getFileData(file.getAbsolutePath(), false);
                    break lookingThroughFlavours;
                }

            } catch (Exception e) { e.printStackTrace();}
        }

        // Inform that the drop is complete
        event.dropComplete(true);
        
    }

    public void dragEnter(DropTargetDragEvent event) {
    	label.setText(">> Drop Input File Here <<");
    }

    public void dragExit(DropTargetEvent event) {
    	label.setText("Drop Input File Here");
    }

    
    public void dragOver(DropTargetDragEvent event) {}

    public void dropActionChanged(DropTargetDragEvent event) {}

}
