package trees;

import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

public class FamilyTree {

    private static class TreeNode {
        private String name;
        private TreeNode parent;
        private ArrayList<TreeNode> children;

        TreeNode(String name) {
            this.name = name;
            children = new ArrayList<>();
        }

        String getName() {
            return name;
        }

        void addChild(TreeNode childNode) {
            // Add childNode to this node's children list. Also
            // set childNode's parent to this node.
            children.add(childNode);
            childNode.parent = this;
        }

        // Searches subtree at this node for a node
        // with the given name. Returns the node, or null if not found.
        TreeNode getNodeWithName(String targetName) {
            // Does this node have the target name?
            if (this.name.equals(targetName))
                return this;
                    
            // No, recurse. Check all children of this node.
            for (TreeNode child: children)
            {
                TreeNode result = child.getNodeWithName(targetName);
                if (result != null)
                    return result;
            }
            
            // Not found anywhere.
            return null;
        }

        // Returns a list of ancestors of this TreeNode, starting with this node’s
        // parent and
        // ending with the root. Order is from recent to ancient.
        ArrayList<TreeNode> collectAncestorsToList() {
            ArrayList<TreeNode> ancestors = new ArrayList<>();
            TreeNode current = this.parent;
            while (current != null) {
                ancestors.add(current);
                current = current.parent;
            }
            return ancestors;
        }

        public String toString() {
            return toStringWithIndent("");
        }

        private String toStringWithIndent(String indent) {
            String s = indent + name + "\n";
            indent += "  ";
            for (TreeNode childNode : children)
                s += childNode.toStringWithIndent(indent);
            return s;
        }
    }

    private TreeNode root;

    //
    // Displays a file browser so that user can select the family tree file.
    //
    public FamilyTree() throws IOException, TreeException {
        // User chooses input file. This block doesn't need any work.
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Family tree text files", "txt");
        File dirf = new File("data");
        if (!dirf.exists()) dirf = new File(".");

        JFileChooser chooser = new JFileChooser(dirf);
        chooser.setFileFilter(filter);
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) System.exit(1);
        File treeFile = chooser.getSelectedFile();

        // Parse the input file. Create a FileReader that reads treeFile. Create a BufferedReader
        // that reads from the FileReader.
        FileReader fr = new FileReader(treeFile);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null)
            addLine(line);
        br.close();
        fr.close();
    }

    //
    // Line format is "parent:child1,child2 ..."
    // Throws TreeException if line is illegal.
    //
    private void addLine(String line) throws TreeException
    {
        // Extract parent and array of children.
        int colonIndex = line.indexOf(":"); //should be the index of the colon in line.
        if (colonIndex < 0){
            //?? throw a TreeException with a useful message
            throw new TreeException("No colon in this line");
        }
        String parent = line.substring(0, colonIndex); //The substring of line that starts at char #0 and ends just before colonIndex. Check the API for 
                           //class java.util.String, method substring(), if you need guidance.
        String childrenString = line.substring(colonIndex + 1, line.length()); //The substring of line that starts just after colonIndex and goes through the end of
                                   //the line. You'll use a different version of substring().
        String[] childrenArray = childrenString.split(","); //Call childrenString.split(). Check the API for details. The result will be an array
                                    //of strings, with the separating commas thrown away.
        
        // Find parent node. If root is null then the tree is empty and the
        // parent node must be constructed. Otherwise the parent node should be 
        // somewhere in the tree.
        TreeNode parentNode;
        if (root == null)
            parentNode = root = new TreeNode(parent);
        else
        {
            parentNode = root.getNodeWithName(parent);  //There's a method in Node that searches for a named node. 
            //??? If the parent node wasn't found, there must have been something wrong in the 
                //data file. Throw an exception.
        }
        
        // Add child nodes to parentNode.
        //?? For each name in childrenArray, create a new node and add that node to parentNode.
        for (String name : childrenArray){
            TreeNode node = new TreeNode(name);
            parentNode.addChild(node);
        }
    }

    // Returns the "deepest" node that is an ancestor of the node named name1, and
    // also is an
    // ancestor of the node named name2.
    //
    // "Depth" of a node is the "distance" between that node and the root. The depth
    // of the root is 0. The
    // depth of the root's immediate children is 1, and so on.
    //
    TreeNode getMostRecentCommonAncestor(String name1, String name2) throws TreeException
    {
        // Get nodes for input names.
        TreeNode node1 = root.getNodeWithName(name1);        // node whose name is name1
        if (node1 == null)
        {
            throw new TreeException("there is no node with name of " + name1);
        }
        TreeNode node2 = root.getNodeWithName(name2);        // node whose name is name2
        if (node2 == null){
            throw new TreeException("there is no node with name of " + name2);
        }
        
        // Get ancestors of node1 and node2.
        ArrayList<TreeNode> ancestorsOf1 = node1.collectAncestorsToList();
        ArrayList<TreeNode> ancestorsOf2 = node2.collectAncestorsToList();
        
        // Check members of ancestorsOf1 in order until you find a node that is also
        // an ancestor of 2. 
        for (TreeNode n1: ancestorsOf1)
            if (ancestorsOf2.contains(n1))
                return n1;
        
        // No common ancestor.
        return null;
    }

    public String toString() {
        return "Family Tree:\n\n" + root;
    }

    public static void main(String[] args) {
        try {
            FamilyTree tree = new FamilyTree();
            System.out.println("Tree:\n" + tree + "\n**************\n");
            TreeNode ancestor = tree.getMostRecentCommonAncestor("Bilbo", "Frodo");
            System.out.println("Most recent common ancestor of Bilbo and Frodo is " + ancestor.getName());
        } catch (IOException x) {
            System.out.println("IO trouble: " + x.getMessage());
        } catch (TreeException x) {
            System.out.println("Input file trouble: " + x.getMessage());
        }
    }
}
