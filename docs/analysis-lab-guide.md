# AnalysisLab — Finding Neighbours for the Interaction Diagram

## Goal

For each class in the diagram, find its direct neighbours — classes it directly calls or that directly call it. This determines which classes get marked NEXT in the Fig 7.9 algorithm.

---

## 1. Finding who calls a method (upstream neighbours)

Place your cursor on the method name (e.g. `setScaleFactor` in `DrawingView.java`).

**Right-click → Find Usages** (`Alt+F7`)

This lists every class that calls that method. Each result is a direct upstream neighbour — add them to the diagram and mark them NEXT.

> Used to find: `ZoomAction`, `ZoomEditorAction`, `ViewToolBar`, `NetView` as neighbours of `DrawingView.setScaleFactor()`.

---

## 2. Finding what a method calls (downstream neighbours)

Read the method body directly in the source file. Each method call on another class object is a downstream neighbour.

Example — `DefaultDrawingView.setScaleFactor()`:
```java
validateViewTranslation();   // internal
invalidateHandles();         // → calls handle.dispose() on AbstractHandle instances
revalidate();                // Swing
repaint();                   // Swing → paintComponent() → drawing.draw()
firePropertyChange(...);     // Swing
```

Each external class whose method is called is a direct downstream neighbour.

---

## 3. Finding who instantiates a class (constructor callers)

Place cursor on the class name or constructor.

**Right-click → Find Usages** (`Alt+F7`)

Filter for constructor usages to find who creates instances of the class.

> Used to find: `ButtonFactory` as a neighbour of `ZoomAction`.

---

## 4. Finding implementations of an interface

Place cursor on the interface name (e.g. `DrawingView`).

**Right-click → Go To → Implementations** (`Ctrl+Alt+B`)

Shows all concrete classes that implement the interface — these are neighbours if the interface method signature changes.

> Used to find: `DefaultDrawingView` as the implementation of `DrawingView`.

---

## What is NOT a neighbour

- **Inheritance (extends)** — a superclass is not a neighbour in the interaction diagram. It only becomes relevant when processing the subclass itself.
  - e.g. `AbstractDrawingViewAction` is not a neighbour of `DrawingView`, only of `ZoomAction`.
- **Indirect Swing callbacks** — `repaint()` triggers `paintComponent()` via the Swing framework, not a direct call. Figures redrawn this way are neighbours of the `Drawing` object, not of `DefaultDrawingView` directly.

---

## Decision for each NEXT class

Once you find a neighbour and mark it NEXT, decide its mark:

| Mark | When |
|---|---|
| CHANGED | The class must modify its own code (e.g. its call site uses the changed signature) |
| PROPAGATES | The class passes the call through without changing its own logic |
| UNCHANGED | The class is affected (called/notified) but needs no code changes and does not propagate further |