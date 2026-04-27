[IntroLab]

- fork created
- maven installed
- project dependencies installed using ``` mvn clean install -DskipTests ```
- project ran using ``` mvn exec:java "-Dexec.mainClass=org.jhotdraw.samples.svg.Main" ```

[ChangeReqLab]

Folloving user stories representing existing features were added to GitHub Projects: 

- As a user, I want to create, open, and close drawings so that I can manage my work sessions efficiently

- As a user, I want to undo and redo my recent actions so that I can correct mistakes without losing progress

- As a user, I want to click on figures to select them so that I can move, resize, or edit them

- As a user, I want to create and edit multi-line text areas on the canvas so that I can add descriptive labels or paragraphs to my drawing

- As a user, I want to zoom in/out and toggle a grid overlay so that I can work with precision at different detail levels


[CLLab]

Objectives: 
• Apply the IDE Debugger to locate feature concepts at runtime.
• Create a list of initial set of classes of the concept location results.

User story featured: As a user, I want to zoom in/out and toggle a grid overlay so that I can work with precision at different detail levels

Features in the story: Zooming, Grid display


Feature zooming: 

  ┌────────────────────────────────────────────────────────────────┬────────────────────────────────────────────────────────────────────────────────────────────────┐  
  │                          Domain Class                          │                                         Responsibility                                         │
  ├────────────────────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ org.jhotdraw.gui.action.ZoomAction                             │ Applies a selected scale factor to a specific DrawingView when the user picks a zoom level     │
  ├────────────────────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ org.jhotdraw.gui.action.ButtonFactory                          │ Factory that creates the zoom popup button with preset zoom levels (5%–400%)                   │  
  ├────────────────────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────┤  
  │ org.jhotdraw.draw.DefaultDrawingView                           │ Concrete drawing canvas view — applies the scale factor transform and triggers repaint at the  │  
  │                                                                │ new zoom level                                                                                 │  
  ├────────────────────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ org.jhotdraw.draw.figure.AbstractAttributedCompositeFigure     │ Base class for composite figures with attributes — redraws/recalculates figure bounds during   │  
  │                                                                │ zoom repaint                                                                                   │
  ├────────────────────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────┤  
  │ org.jhotdraw.draw.AttributeKey                                 │ Type-safe key for figure attributes — used to read visual attributes (stroke width, colors,    │
  │                                                                │ etc.) during figure redraw at new scale                                                        │  
  ├────────────────────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ org.jhotdraw.draw.handle.AbstractHandle                        │ Base class for selection handles — recalculates handle screen positions when zoom changes      │  
  │                                                                │ (handles must stay a fixed pixel size)                                                         │  
  ├────────────────────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ org.jhotdraw.draw.handle.BezierNodeHandle                      │ Handle for Bezier path control points — redraws at correct screen position after zoom scale    │  
  │                                                                │ change                                                                                         │  
  ├────────────────────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────┤
  │ org.jhotdraw.draw.figure.AbstractFigure                        │ Base class for all figures — provides invalidation and repaint notification when the view      │  
  │                                                                │ scale changes                                                                                  │
  ├────────────────────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────┤  
  │ org.jhotdraw.gui.action.AbstractDrawingViewAction$EventHandler │ Inner PropertyChangeListener that listens for view/editor property changes and updates the     │  
  │                                                                │ ZoomAction's enabled state                                                                     │
  └────────────────────────────────────────────────────────────────┴────────────────────────────────────────────────────────────────────────────────────────────────┘  
                                                                                                                                                                     
  The last 4 classes (AbstractAttributedCompositeFigure, AbstractHandle, BezierNodeHandle, AbstractFigure) appeared because after setScaleFactor() is called, the      
  entire canvas repaints — every visible figure and its handles must redraw themselves at the new scale. That's why they show up in your trace even though they're not
  "zoom logic" per se — they're the repaint chain triggered by the zoom.                                                                                               

Feature grid display: 

to be finished

[AnalysisLab]

Objectives: Apply static and dynamic analysis to find the estimated impacted set of classes based on your Change request.

Classwork: Find The Estimated Impact Set of classes by following the activities illustrated in Figure 7.9

Portfolio Work: Use Table 1 to list the packages and the number of classes you visited after you located the concept. Write short comments explaining what you have learned about each package and how they contribute to your feature?

If we changed the setScaleFactor method signature, the estimated impact set would include all classes that call this method directly or indirectly. This would likely include:
- org.jhotdraw.gui.action.ZoomAction (directly calls setScaleFactor on the Drawing
- org.jhotdraw.draw.DefaultDrawingView (calls setScaleFactor to apply the zoom level)
- org.jhotdraw.draw.figure.AbstractAttributedCompositeFigure (repaints itself when the view scale changes)
- org.jhotdraw.draw.handle.AbstractHandle (recalculates handle positions when zoom changes)
- org.jhotdraw.draw.handle.BezierNodeHandle (recalculates control point handle positions when zoom changes)
- org.jhotdraw.draw.figure.AbstractFigure (invalidates and repaints when the view scale changes)
- org.jhotdraw.gui.action.AbstractDrawingViewAction$EventHandler (listens for property changes that may affect the ZoomAction's enabled state)

[CILab]

Introduction: In software engineering, continuous integration (CI) is the practice of merging
all developers’ working copies to a shared mainline several times a day [Tho]. Grady Booch first
proposed the term CI in his 1991 method although he did not advocate integrating several times
a day [Boo]. Extreme programming (XP) adopted the concept of CI and did advocate integrating
more than once per day – perhaps as many as tens of times per day [Bec99].

Objectives:
• Understand what CI is.
• Setup a simple CI pipeline.

Classwork:
1. Go to [Building and testing Java with Maven]
2. Add a *.yml file to your repository path <YOUR_PROJECT>/.github/workflows/ to tell
   GitHub Actions CI what to do.
3. Configure the *.yml to automatically build your project for each pull request (use maven).
4. To use shared jars from GitHub Packages you need create a .maven-settings.xml file in
   the project root folder, see [Working with the Apache Maven registry]
5. Configure the *.yml to execute tests automatically.

- see .github/workflows/ 

[RefactoringLab]
Refactoring is a disciplined technique for restructuring an existing body of code,
altering its internal structure without changing its external behavior. Its heart is a series of small
behavior preserving transformations. Each transformation (called a ”refactoring”) does little, but a
sequence of these transformations can produce a significant restructuring. Since each refactoring is
small, it’s less likely to go wrong. The system is kept fully working after each refactoring, reducing
the chances that a system can get seriously broken during the restructuring.

Objectives:
Identify and understand Bad Code Smells.
Apply refactorings to get rid of Bad Code.

Classwork:
Make sure you have your own feature branch, using “git checkout -b your-feature development”.
Please follow the feature branch workflow [GitHub flow].
Install [sonarlint].
Find Code smells in JHotDraw based on your change request and sonarlint (shouldn’t be a problem).
Apply one or more suitable Refactoring Patterns to get rid of the bad code smells.

Portfolio Work:
Describe the code smell that triggered your refactoring, see Chapter 4 in [Ker05]. Describe what you plan to change by refactoring. Describe the strategy of the refactorings. Which of
the refactorings from [Ker05] did you apply and what was the reasoning behind it?
• Remember to describe the strategies and purpose of the Refactorings.

Refactor 1: 
Commit message: 
refactor: jhotdraw-gui/src/main/java/org/jhotdraw/gui/action/ButtonFactory.java separated duplicating string fragments to constants
Used SonarQube to find code smells in ButtonFactory.java. Found that the string fragments for zoom levels were duplicated in the code. Refactored by extracting these string fragments into constants to improve maintainability and reduce the risk of typos or inconsistencies in the future. This refactoring follows the "Extract Constant" pattern from [Ker05], which helps to centralize commonly used values and makes the code easier to read and maintain.
Used Alt + J to select all occurrences of the string fragments and then extracted them into constants using the "Extract Constant" refactoring in IntelliJ IDEA. This approach ensures that any future changes to the zoom level strings only need to be made in one place, improving code maintainability and reducing the likelihood of errors.

Refactor 2: 
In jhotdraw-core/src/main/java/org/jhotdraw/draw/DefaultDrawingView.java there are fields that need to be either serialzed or marked transient. 
This was not resolved in this portfolio, as it is not one of the listed code smells in the presentation.
However, it will crash the app if resources run out and the GC tries to serialize the view.

SonarQube: 
1. Duplicated Code                                                                                                                                                                                                                                                                                                                                        
   Look for identical or near-identical blocks across methods/classes. Ctrl+F a suspicious snippet.
   SonarLint: Yes — flags duplicate blocks and repeated literals (java:S1192, S4144)

2. Long Method                                                                                                                                                                                                                                                                                                                                            
   Scroll through methods — if it doesn't fit on one screen, it's too long. Rule of thumb: >20 lines.                                                                                                                                                                                                                                                        
   SonarLint: Yes — flags methods exceeding line threshold (java:S138)

3. Large Class                                                                                                                                                                                                                                                                                                                                            
   Count fields and methods. >10 fields or >20 methods is a signal.                                                                                                                                                                                                                                                                                        
   SonarLint: Partial — flags too many methods (java:S6539)

4. Long Parameter List                                                                                                                                                                                                                                                                                                                                    
   Look for method signatures with >3–4 parameters.                                                                                                                                                                                                                                                                                                        
   SonarLint: Yes — flags methods with too many parameters (java:S107)

5. Divergent Change                                                                                                                                                                                                                                                                                                                                       
   Read the class and ask: "does this class change for multiple unrelated reasons?" Manual only.                                                                                                                                                                                                                                                             
   SonarLint: No

6. Shotgun Surgery                                                                                                                                                                                                                                                                                                                                        
   Look at git history (git log --follow -p <file>) — if a single concern touches many files. Manual only.                                                                                                                                                                                                                                                 
   SonarLint: No

7. Feature Envy                                                                                                                                                                                                                                                                                                                                           
   Find methods that call many methods/fields of another class more than their own.                                                                                                                                                                                                                                                                        
   SonarLint: No (some tools like JDeodorant detect it)

8. Data Clumps                                                                                                                                                                                                                                                                                                                                            
   Look for the same 3+ parameters appearing together in multiple method signatures.                                                                                                                                                                                                                                                                         
   SonarLint: No — purely manual

Refactor 3:
File: jhotdraw-core/src/main/java/org/jhotdraw/draw/DefaultDrawingView.java

Code smell — Long Method and Duplicated Code:
SonarQube flagged two methods in DefaultDrawingView — drawDrawingVolatileBuffered and drawDrawingNonvolatileBuffered — for excessive Cognitive Complexity (35 and 25 respectively, against the allowed threshold of 15). Both methods mixed three distinct responsibilities in a single body: managing the buffered area geometry, validating and recreating the image buffer, and repainting dirty regions. The dirty-area repainting logic was also duplicated verbatim between the two methods, differing only in the buffer type (VolatileImage vs BufferedImage). These are the Long Method and Duplicated Code smells from Chapter 4 of [Ker05].

Plan:
Decompose both methods by extracting the distinct responsibilities into named helper methods, and unify the duplicated repainting logic into a shared implementation so that the two buffer paths delegate to the same code.

Strategy and refactorings applied — Extract Method [Ker05]:
The Extract Method refactoring was applied repeatedly, each time isolating one coherent sub-task:

1. updateBufferedAreaShift(Point shift, Rectangle vr) — extracts the logic that calculates how much the buffered area has shifted relative to the visible rectangle and marks the newly uncovered edges as dirty. Naming this makes the intent clear and removes the nesting that was inflating the CC of the outer method.

2. resizeAndInvalidateBuffer(Rectangle vr) — extracts the else-branch that handles the case where the buffer dimensions no longer match the visible rect: it resets the buffered area bounds, marks everything dirty, and discards the old buffer object. Previously this was an anonymous else-block with no label.

3. validateBuffer(Rectangle vr) — extracts the switch statement that re-creates or marks-dirty the VolatileImage based on its validation state (IMAGE_INCOMPATIBLE / IMAGE_RESTORED). Isolating this removes the switch and its nested try/catch from the outer while-loop, which was the largest single contributor to the high CC score. The switch was also replaced with if/else statements as advised by SonarQube, since only two cases were present.

4. paintDirtyBufferAreaToGraphics(Graphics2D, int, int, Point) — extracts the dirty-region repainting logic (composite setup, optional copy-area shift, clip, clear, redraw) into a single shared implementation. Two thin overloads — paintDirtyBufferArea(VolatileImage, Point) and paintDirtyBufferArea(BufferedImage, Point) — delegate to this shared method, eliminating the duplication between the volatile and non-volatile paths entirely.

Reasoning:
Each extracted method has a name that replaces a comment that previously explained what the following block of code did. This is the core motivation for Extract Method in [Ker05]: if you need a comment to explain a block, the block deserves its own method. The resulting drawDrawingVolatileBuffered reads as a sequence of named steps, with complexity reduced from 23 to approximately 8, well within the SonarQube threshold.

9. Primitive Obsession                                                                                                                                                                                                                                                                                                                                    
   Look for String, int, boolean used where a small domain class would be clearer (e.g. color as String, coordinate as two ints).                                                                                                                                                                                                                          
   SonarLint: Partial — flags some cases

10. Switch Statements                                                                                                                                                                                                                                                                                                                                     
    Search switch or long if/else if chains that switch on type.                                                                                                                                                                                                                                                                                            
    SonarLint: Partial — flags complex conditionals (java:S1479, S131)

11. Parallel Inheritance Hierarchies                                                                                                                                                                                                                                                                                                                      
    When adding a subclass of A forces adding a subclass of B. Requires reading class hierarchy manually.                                                                                                                                                                                                                                                     
    SonarLint: No

12. Lazy Class                                                                                                                                                                                                                                                                                                                                            
    Classes with very few methods/fields that barely justify their existence.                                                                                                                                                                                                                                                                               
    SonarLint: No

13. Speculative Generality                                                                                                                                                                                                                                                                                                                              
    Look for abstract classes with only one subclass, unused parameters, or overly generic names like AbstractProcessor.                                                                                                                                                                                                                                      
    SonarLint: Partial — flags unused code (java:S1144, S2583)

14. Temporary Field                                                                                                                                                                                                                                                                                                                                       
    Instance variables that are only set in one method and null everywhere else.                                                                                                                                                                                                                                                                              
    SonarLint: Partial — flags some null-related patterns

15. Message Chains                                                                                                                                                                                                                                                                                                                                        
    Look for a.getB().getC().getD() call chains (Law of Demeter violations).                                                                                                                                                                                                                                                                                
    SonarLint: No

16. Middle Man                                                                                                                                                                                                                                                                                                                                            
    A class where most methods just delegate to another class — check if >50% of methods are one-liners that call another object.                                                                                                                                                                                                                             
    SonarLint: No

17. Inappropriate Intimacy                                                                                                                                                                                                                                                                                                                                
    A class accessing private/internal fields or methods of another via excessive coupling.                                                                                                                                                                                                                                                                 
    SonarLint: Partial — flags some coupling and visibility issues

18. Alternative Classes with Different Interfaces                                                                                                                                                                                                                                                                                                         
    Two classes doing the same thing with different method names. Manual comparison.                                                                                                                                                                                                                                                                        
    SonarLint: No

19. Incomplete Library Class                                                                                                                                                                                                                                                                                                                              
    A library class missing functionality, forcing you to add utility methods elsewhere. Manual.                                                                                                                                                                                                                                                              
    SonarLint: No

20. Data Class                                                                                                                                                                                                                                                                                                                                            
    Classes with only fields + getters/setters and no real behavior.                                                                                                                                                                                                                                                                                        
    SonarLint: No (Checkstyle can help)

21. Refused Bequest                                                                                                                                                                                                                                                                                                                                       
    A subclass that ignores or overrides most of what it inherits. Look for empty or exception-throwing overrides.                                                                                                                                                                                                                                            
    SonarLint: Partial — flags some cases (java:S2177)

22. Comments                                                                                                                                                                                                                                                                                                                                              
    Blocks of commented-out code, or comments explaining what rather than why.                                                                                                                                                                                                                                                                                
    SonarLint: Yes — flags commented-out code (java:S125) 

!!!!! IMPORTANT !!!!! 
Do more refactoring practice before the exam. 

