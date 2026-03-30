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

