import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class DemoPanel extends JPanel {
    // Screen Settings:
    final int maxColumn = 25;
    final int maxRow = 25;
    final int nodeSize = 65;
    final int screenWidth = nodeSize * maxColumn;
    final int screenHeight = nodeSize * maxRow;

    // Node:
    Node[][] node = new Node[maxColumn][maxRow];
    Node startNode, goalNode, currentNode;
    ArrayList<Node> openList = new ArrayList<>();
    ArrayList<Node> checkedList = new ArrayList<>();

    boolean goalReached = false;
    int step = 0;

    public DemoPanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setLayout(new GridLayout(maxRow, maxColumn));
        this.addKeyListener(new KeyHandler(this));
        this.setFocusable(true);

        // Placing Nodes on DemoPanel:
        int col = 0;
        int row = 0;

        while (col < maxColumn && row < maxRow) {
            node[col][row] = new Node(col, row);
            this.add(node[col][row]);
            col++;

            if (col == maxColumn) {
                col = 0;
                row++;
            }
        }


        // Setting Start and Goal Nodes:
        int start_x = generateRandomINT(maxColumn);
        int start_y = generateRandomINT(maxRow);
        int goal_x = generateRandomINT(maxColumn);
        int goal_y = generateRandomINT(maxRow);

        while (start_x == goal_x && start_y == goal_y) {
            goal_x = generateRandomINT(maxColumn);
        }

        setStartNode(start_x, start_y);
        setGoalNode(goal_x, goal_y);

        // Setting up Obstacles:
        int obstacle_count = 0;
        while (obstacle_count < 250) {
            int x = generateRandomINT(maxColumn);
            int y = generateRandomINT(maxRow);

            if ((x != start_x || y != start_y) && (x != goal_x || y != goal_y)) {
                setSolidNode(x, y);
                obstacle_count++;
            }
        }

        // Setting Costs:
        setCostOnNodes();
    }

    private int generateRandomINT(int bound) {
        Random random_int = new Random();
        return (random_int.nextInt(bound));
    }
    private void setStartNode(int col, int row) {
        node[col][row].setAsStart();
        startNode = node[col][row];
        currentNode = startNode;
    }

    private void setGoalNode(int col, int row) {
        node[col][row].setAsGoal();
        goalNode = node[col][row];
    }

    private void setSolidNode(int col, int row) {
        node[col][row].setAsSolid();
    }

    private void setCostOnNodes() {
        int col = 0;
        int row = 0;

        while (col < maxColumn && row < maxRow) {
            getCost(node[col][row]);
            col++;

            if (col == maxColumn) {
                col = 0;
                row++;
            }
        }
    }

    // Calculating Costs:
    private void getCost(Node node) {
        // g-Cost:
        int xDistance = Math.abs(node.col - startNode.col);
        int yDistance = Math.abs(node.row - startNode.row);

        node.gCost = xDistance + yDistance;

        // h-Cost:
        xDistance = Math.abs(node.col - goalNode.col);
        yDistance = Math.abs(node.row - goalNode.row);

        node.hCost = xDistance + yDistance;

        // f-Cost:
        node.fCost = node.gCost + node.hCost;

        // Displaying the Cost:
        if (node != startNode && node != goalNode) {
            node.setText("<html>f: " + node.fCost + "<br>g: " + node.gCost + "</html>");
        }
    }

    public void search() {
        if (!goalReached) {
            int col = currentNode.col;
            int row = currentNode.row;

            currentNode.setAsChecked();
            checkedList.add(currentNode);
            openList.remove(currentNode);

            // Open the UPPER Node:
            if (row - 1 >= 0) {
                openNode(node[col][row - 1]);
            }

            // Open the LEFT Node:
            if (col - 1 >= 0) {
                openNode(node[col - 1][row]);
            }

            // Open the BOTTOM Node:
            if (row + 1 < maxRow) {
                openNode(node[col][row + 1]);
            }

            // Open the RIGHT Node:
            if (col + 1 < maxColumn) {
                openNode(node[col + 1][row]);
            }

            // Finding the Node with the Best Cost:
            // Initial Values for best Node INDEX and COST:
            int bestNodeIndex = 0;
            int bestNodeFCost = 999;

            for (int i = 0; i < openList.size(); i++) {
                // Checking if this Node's f-Cost is better:
                if (openList.get(i).fCost < bestNodeFCost) {
                    bestNodeIndex = i;
                    bestNodeFCost = openList.get(i).fCost;
                }

                // If f-Cost is Equal, check the g-Cost:
                else if (openList.get(i).fCost == bestNodeFCost) {
                    if (openList.get(i).gCost < openList.get(bestNodeIndex).gCost) {
                        bestNodeIndex = i;
                    }
                }
            }

            // Now, we have the Best Node as bestNodeIndex.
            currentNode = openList.get(bestNodeIndex);

            if (currentNode == goalNode) {
                goalReached = true;
                trackThePath();
            }
        }
    }

    public void autoSearch() {
        while (!goalReached && step < 300) {
            int col = currentNode.col;
            int row = currentNode.row;

            currentNode.setAsChecked();
            checkedList.add(currentNode);
            openList.remove(currentNode);

            // Open the UPPER Node:
            if (row - 1 >= 0) {
                openNode(node[col][row - 1]);
            }

            // Open the LEFT Node:
            if (col - 1 >= 0) {
                openNode(node[col - 1][row]);
            }

            // Open the BOTTOM Node:
            if (row + 1 < maxRow) {
                openNode(node[col][row + 1]);
            }

            // Open the RIGHT Node:
            if (col + 1 < maxColumn) {
                openNode(node[col + 1][row]);
            }

            // Finding the Node with the Best Cost:
            // Initial Values for best Node INDEX and COST:
            int bestNodeIndex = 0;
            int bestNodeFCost = 999;

            for (int i = 0; i < openList.size(); i++) {
                // Checking if this Node's f-Cost is better:
                if (openList.get(i).fCost < bestNodeFCost) {
                    bestNodeIndex = i;
                    bestNodeFCost = openList.get(i).fCost;
                }

                // If f-Cost is Equal, check the g-Cost:
                else if (openList.get(i).fCost == bestNodeFCost) {
                    if (openList.get(i).gCost < openList.get(bestNodeIndex).gCost) {
                        bestNodeIndex = i;
                    }
                }
            }

            // Now, we have the Best Node as bestNodeIndex.
            currentNode = openList.get(bestNodeIndex);

            if (currentNode == goalNode) {
                goalReached = true;
                trackThePath();
            }
        }
        step++;
    }

    private void openNode(Node node) {
        if (!node.open && !node.checked && !node.solid) {

            // If Node is not Opened yet, add it to the Open List:
            node.setAsOpen();
            node.parent = currentNode;
            openList.add(node);
        }
    }

    private void trackThePath() {
        // Backtracking and Drawing the Best Path:
        Node current = goalNode;

        while (current != startNode) {
            current = current.parent;

            if (current != startNode) {
                current.setAsPath();
            }
        }
    }
}
