# 01 [IntroLab]

- fork created
- maven installed
- project dependencies installed using ``` mvn clean install -DskipTests ```
- project ran using ``` mvn exec:java "-Dexec.mainClass=org.jhotdraw.samples.svg.Main" ```

# 02.1 [ChangeReqLab]

Following user stories representing existing features were added to GitHub Projects: 

- As a user, I want to create, open, and close drawings so that I can manage my work sessions efficiently

- As a user, I want to undo and redo my recent actions so that I can correct mistakes without losing progress

- As a user, I want to click on figures to select them so that I can move, resize, or edit them

- As a user, I want to create and edit multi-line text areas on the canvas so that I can add descriptive labels or paragraphs to my drawing

- As a user, I want to zoom in/out and toggle a grid overlay so that I can work with precision at different detail levels

# 02.2 [CLLab]

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


# 03.1 [AnalysisLab]

**Change request analysed:** As a user, I want to undo and redo my recent actions so that I can correct mistakes without losing progress.

The concept location is `org.jhotdraw.undo.UndoRedoManager`. Static analysis showed that drawing changes are reported as `UndoableEdit` events, collected by the manager, and then executed through its Undo and Redo actions. Following the dynamic path in the Draw sample confirmed that `DrawView` registers the manager as a listener on the drawing, exposes the manager actions, and updates unsaved-change state when the history changes. The edit classes were included because they restore the actual figure state during undo and redo. The Maven reactor check compiled this path and passed the available 6 tests; the repository contains no dedicated DrawView undo/redo test.

### Table 1: Packages visited during impact analysis

| Package name | # of classes | Comments |
|---|---:|---|
| `org.jhotdraw.undo` | 2 | `UndoRedoManager` stores the edit history, updates action state, and performs undo/redo; `CompositeEdit` groups related edits into one history entry. |
| `org.jhotdraw.draw` | 2 | `Drawing` defines the undoable-edit listener contract and `AbstractDrawing` broadcasts edit events to the manager. |
| `org.jhotdraw.draw.event` | 5 | `AttributeChangeEdit`, `CompositeFigureEdit`, `SetBoundsEdit`, `TransformEdit`, and `TransformRestoreEdit` capture changes and restore or reapply figure state. |
| `org.jhotdraw.draw.figure` | 1 | `AbstractFigure` creates `SetBoundsEdit` events when figure bounds change, making resize and reposition operations undoable. |
| `org.jhotdraw.draw.handle` | 6 | Figure handles create transform, attribute, and composite edits during direct manipulation such as moving, rotating, and editing control points. |
| `org.jhotdraw.draw.tool` | 2 | `AbstractTool` supports edit listeners and `DefaultDragTracker` creates transform edits for drag operations. |
| `org.jhotdraw.draw.action` | 5 | Alignment, movement, and attribute actions create the edits that the manager later records as undoable user operations. |
| `org.jhotdraw.action.edit` | 2 | `UndoAction` and `RedoAction` connect the application Edit menu to the active view's real actions and mirror their enabled state. |
| `org.jhotdraw.samples.draw` | 2 | `DrawingPanel` wires a drawing to an `UndoRedoManager`; `DrawView` owns the manager, installs the actions, and synchronizes unsaved changes. This is the dynamic execution path used for the estimate. |

**Estimated impact set:** 27 production classes across 9 packages. The set includes the classes visited while following event production, event delivery, history management, action presentation, and the Draw sample integration. Other sample views use the same wiring pattern, but were not counted because they are alternative application integrations rather than part of the selected Draw sample path.

# 04 [RefactLab]

**User story used for refactoring:** As a user, I want to undo and redo my recent actions so that I can correct mistakes without losing progress.

## Classwork

- SonarLint was used on the core class in the undo/redo path: [UndoRedoManager.java](..\jhotdraw-utils\src\main\java\org\jhotdraw\undo\UndoRedoManager.java).
- Refactoring target was selected from the change-request concept location (`org.jhotdraw.undo.UndoRedoManager`).

## Portfolio Work

### Code smell that triggered refactoring (Ker05 Chapter 4)

In [UndoRedoManager.java](..\jhotdraw-utils\src\main\java\org\jhotdraw\undo\UndoRedoManager.java), there were maintainability smells in undo/redo control logic:

- duplicated method bodies in `undo()`, `redo()`, and `undoOrRedo()` (same flag-handling and action refresh structure)
- duplicated action state/label update logic in `updateActions()`

These match [Ker05] Chapter 4 concerns around duplicated logic and methods that communicate intent poorly because repeated low-level steps obscure the main behavior.

### What I planned to change

I planned to preserve behavior while making the flow intention-revealing:

1. Extract the repeated undo/redo execution wrapper into one method.
2. Extract repeated action-label update steps into one method.
3. Keep public API unchanged so calling code and user-visible behavior stay stable.

### Refactoring strategy

I applied an incremental strategy:

1. Identify exact duplicated blocks.
2. Introduce private helper methods.
3. Replace duplicated code in place, one method at a time.
4. Compile and verify.

File refactored: - [UndoRedoManager.java](..\jhotdraw-utils\src\main\java\org\jhotdraw\undo\UndoRedoManager.java)

Validation run:

- `mvn -pl jhotdraw-utils -am -DskipTests compile` (build success)

### Refactoring pattern(s) from Ker05 and reasoning

- **Compose Method**: applied to make high-level operations (`undo`, `redo`, `undoOrRedo`) read as one clear step by delegating repeated mechanics.
	- Reasoning: the previous methods repeated the same scaffolding (set flag, call super, reset flag, update actions) and hid the core intent.

- **Extract Method** (mechanics used to realize Compose Method):
	- `performUndoRedo(int operation)` centralizes guarded execution and post-update behavior.
	- `updateActionState(AbstractAction action, boolean canPerform, String presentationName, String fallbackKey)` centralizes repeated action-label state changes.

### Purpose of the refactoring

The purpose was to improve maintainability and reduce defect risk in the undo/redo feature path by:

- removing duplicated logic
- improving readability of undo/redo operations
- making future changes to action updates and guard logic occur in one place


# 05 [ActLab]


## Actualization

Undo/redo is implemented through `UndoRedoManager`, drawing changes are delivered as `UndoableEdit` events by `Drawing` and `AbstractDrawing`, and `DrawView` connects the manager to the active drawing and application actions. The refactoring also propagated the shared execution and action-state logic into private helpers without changing the public API.

## Clean Architecture

JHotDraw separates responsibilities into collaborating layers:

- **Core domain:** `Drawing`, figures, handles, and edit classes represent drawings and their state changes.
- **Application/use-case coordination:** `UndoRedoManager` coordinates undo and redo history and exposes actions for the editor.
- **Interface/application integration:** `DrawView` connects the drawing, manager, menus, and unsaved-change state.
- **Infrastructure:** Swing actions, resource bundles, file formats, and Maven modules provide framework and I/O details.

The dependency direction keeps the drawing model independent of `DrawingView`, `DrawingEditor`, and tools. Changes are communicated through `UndoableEdit` and listener contracts, so the model does not depend on the user-interface implementation. This makes the undo/redo behavior reusable and limits change propagation to the integration points that actually consume the contract.

## Clean Code principles

- **Single responsibility:** edit classes restore specific kinds of state, while `UndoRedoManager` owns history and action state.
- **Meaningful names:** `performUndoRedo` and `updateActionState` express intent more clearly than repeating low-level operations.
- **Small methods:** `undo`, `redo`, and `undoOrRedo` delegate common mechanics to focused private helpers.
- **DRY:** shared undo/redo guarding and action-label updates are implemented once.
- **Preserved interfaces:** public manager methods remain stable while internal duplication is removed.

## SOLID examples

| Principle | Example in JHotDraw |
|---|---|
| **S - Single Responsibility** | `UndoRedoManager` manages history and action state; `TransformEdit` restores transform changes; `DrawView` performs application wiring. |
| **O - Open/Closed** | New `UndoableEdit` implementations can represent additional operations without changing the manager's history protocol. |
| **L - Liskov Substitution** | Concrete `UndoableEdit` types are handled through the `UndoableEdit` contract and can be undone or redone by the manager. |
| **I - Interface Segregation** | `Drawing` exposes focused listener and drawing contracts, while views, tools, and formats use the interfaces relevant to their roles. |
| **D - Dependency Inversion** | The drawing publishes edits through the `UndoableEditListener` abstraction; `AbstractDrawing` does not depend directly on `UndoRedoManager`. |