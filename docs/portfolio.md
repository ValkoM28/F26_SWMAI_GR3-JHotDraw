# 01 [IntroLab]

- fork created
- maven installed
- project dependencies installed using ``` mvn clean install -DskipTests ```
- project ran using ``` mvn exec:java "-Dexec.mainClass=org.jhotdraw.samples.svg.Main" ```

# 02.1 [ChangeReqLab]

Following user stories representing existing features were added to GitHub Projects: 

- As a user, I want to create, open, and close drawings so that I can manage my work sessions efficiently

- `As a user, I want to undo and redo my recent actions so that I can correct mistakes without losing progress`

- As a user, I want to click on figures to select them so that I can move, resize, or edit them

- As a user, I want to create and edit multi-line text areas on the canvas so that I can add descriptive labels or paragraphs to my drawing

- As a user, I want to zoom in/out and toggle a grid overlay so that I can work with precision at different detail levels

# 02.2 [CLLab]

### User story:

```
As a user, I want to undo and redo my recent actions so that I can correct mistakes without losing progress
```

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

# 07 [TestingLab]

JUnit 4.13.2 was added as a test dependency to `jhotdraw-utils`, which owns `UndoRedoManager`. The tests use a small in-memory `AbstractUndoableEdit` stub, so they test the manager's history behavior without depending on drawings, Swing views, or application wiring.

The test class [UndoRedoManagerTest.java](..\jhotdraw-utils\src\test\java\org\jhotdraw\undo\UndoRedoManagerTest.java) covers:

- the best-case undo and redo sequence, including edit state and action enabled state
- the empty-history boundaries, where undo and redo throw their expected exceptions
- the boundary where adding a new edit after undo removes the redo branch
- discarding all edits, including resetting significant-edit state and actions

Java `assert` statements are used for manager/edit invariants in addition to JUnit assertions. A test-only label bundle supplies the manager's action labels while keeping the unit tests isolated from the core and application modules.

## Verification

The focused Maven test command passed:

`mvn -pl jhotdraw-utils -Dtest=UndoRedoManagerTest test`

Result: 5 tests run, 0 failures, 0 errors.

# 09 [TestLab2]

## Mapping User Stories to BDD:

| User Story | BDD Scenario |
|---|---|
| As a user, I want to create, open, and close drawings so that I can manage my work sessions efficiently | **Given** a new application session<br>**When** I create a new drawing<br>**Then** I should have an empty canvas ready for editing |
| As a user, I want to undo and redo my recent actions so that I can correct mistakes without losing progress | **Given** I have performed an edit action on the drawing<br>**When** I perform undo<br>**Then** the action should be reversed and redo should be available |
| As a user, I want to click on figures to select them so that I can move, resize, or edit them | **Given** a drawing with multiple figures<br>**When** I click on a specific figure<br>**Then** that figure should be selected and editing handles should appear |
| As a user, I want to create and edit multi-line text areas on the canvas so that I can add descriptive labels or paragraphs to my drawing | **Given** I have a blank canvas<br>**When** I create a text area and enter multiple lines<br>**Then** the text should be displayed with proper line breaks |
| As a user, I want to zoom in/out and toggle a grid overlay so that I can work with precision at different detail levels | **Given** a drawing at 100% zoom level<br>**When** I zoom in to 200%<br>**Then** the drawing should appear larger and details should be more visible |

## BDD Test Implementation

### Test Framework Setup

JGiven 1.3.1 was added as a test dependency to `jhotdraw-utils` along with AssertJ 3.24.2 for fluent assertions and AssertJ-Swing 3.17.1 for Swing UI testing support.

### Implemented Test: Undo/Redo User Story

Test class: [UndoRedoBDDTest.java](..\jhotdraw-utils\src\test\java\org\jhotdraw\undo\UndoRedoBDDTest.java)

The test follows the JGiven pattern with three stages:

- **GivenStage**: Sets up the test context with an UndoRedoManager and test edits
- **WhenStage**: Performs user actions (undo, redo, adding new edits)
- **ThenStage**: Verifies outcomes using AssertJ fluent assertions

### BDD Scenarios Automated

1. **User can undo an action to correct a mistake**
   - **Given** a drawing with undo manager and an edit action has been performed
   - **When** the user performs undo
   - **Then** the action should be reversed, undo should not be available, and redo should be available

2. **User can redo an undone action to restore progress**
   - **Given** a drawing with undo manager, an edit action performed, and the action has been undone
   - **When** the user performs redo
   - **Then** the action should be restored, undo should be available, and redo should not be available

3. **User can undo multiple actions sequentially**
   - **Given** a drawing with undo manager and multiple edit actions have been performed
   - **When** the user performs undo multiple times
   - **Then** all actions should be reversed in reverse order and redo should be available

4. **Performing new action after undo clears redo history**
   - **Given** a drawing with undo manager, an edit action performed, and the action has been undone
   - **When** a new edit action is performed
   - **Then** redo should not be available and the new action should be undoable

### Verification

The BDD tests were verified using:

```powershell
mvn -pl jhotdraw-utils -Dtest=UndoRedoBDDTest test
```

Results:
```powershell
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running org.jhotdraw.undo.UndoRedoBDDTest
SLF4J: No SLF4J providers were found.
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See https://www.slf4j.org/codes.html#noProviders for further details.

Test Class: org.jhotdraw.undo.UndoRedoBDDTest

 Performing new action after undo clears redo history

   Given a drawing with undo manager
     And an edit action has been performed
     And the action has been undone
    When a new edit action is performed
    Then redo should not be available
     And the new action should be undoable


 User can redo an undone action to restore progress

   Given a drawing with undo manager
     And an edit action has been performed
     And the action has been undone
    When the user performs redo
    Then the action should be restored
     And undo should be available
     And redo should not be available


 User can undo an action to correct a mistake

   Given a drawing with undo manager
     And an edit action has been performed
    When the user performs undo
    Then the action should be reversed
     And undo should not be available
     And redo should be available


 User can undo multiple actions sequentially

   Given a drawing with undo manager
     And multiple edit actions have been performed
    When the user performs undo multiple times
    Then all actions should be reversed in reverse order
     And redo should be available

[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.161 s -- in org.jhotdraw.undo.UndoRedoBDDTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  7.104 s
[INFO] Finished at: 2026-08-23T09:26:56Z
[INFO] ------------------------------------------------------------------------
```