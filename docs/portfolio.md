# [IntroLab]

- fork created
- maven installed
- project dependencies installed using ``` mvn clean install -DskipTests ```
- project ran using ``` mvn exec:java "-Dexec.mainClass=org.jhotdraw.samples.svg.Main" ```

# [ChangeReqLab]

Following user stories representing existing features were added to GitHub Projects: 

- As a user, I want to create, open, and close drawings so that I can manage my work sessions efficiently

- As a user, I want to undo and redo my recent actions so that I can correct mistakes without losing progress

- As a user, I want to click on figures to select them so that I can move, resize, or edit them

- As a user, I want to create and edit multi-line text areas on the canvas so that I can add descriptive labels or paragraphs to my drawing

- As a user, I want to zoom in/out and toggle a grid overlay so that I can work with precision at different detail levels

# [CLLab]

**User story:** As a user, I want to undo and redo my recent actions so that I can correct mistakes without losing progress

### Initial table


| Domain Class | Responsibility |
|---|---|
| [AbstractDrawing.java](..\jhotdraw-core\src\main\java\org\jhotdraw\draw\AbstractDrawing.java) | Acts as the drawing model that emits undoable edit events and keeps listeners informed when figures change. |
| [UndoRedoManager.java](..\jhotdraw-utils\src\main\java\org\jhotdraw\undo\UndoRedoManager.java) | Central undo/redo manager that stores edit history and provides the Undo and Redo actions. |
| [CompositeEdit.java](..\jhotdraw-utils\src\main\java\org\jhotdraw\undo\CompositeEdit.java) | Groups multiple edits into a single undoable transaction so related actions can be undone/redone together. |
| [TransformEdit.java](..\jhotdraw-core\src\main\java\org\jhotdraw\draw\event\TransformEdit.java) | Represents a move/transform operation as an undoable edit. |
| [SetBoundsEdit.java](..\jhotdraw-core\src\main\java\org\jhotdraw\draw\event\SetBoundsEdit.java) | Represents a figure bounds change as an undoable edit, allowing resize/reposition reversal. |
| [AttributeChangeEdit.java](..\jhotdraw-core\src\main\java\org\jhotdraw\draw\event\AttributeChangeEdit.java) | Represents attribute changes such as style or appearance updates that can be undone/redone. |
| [DrawView.java](..\jhotdraw-samples\jhotdraw-samples-misc\src\main\java\org\jhotdraw\samples\draw\DrawView.java) | Controller/view class that wires the drawing to the undo manager and tracks whether there are unsaved changes. |
