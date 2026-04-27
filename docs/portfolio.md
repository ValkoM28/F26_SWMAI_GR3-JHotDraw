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

