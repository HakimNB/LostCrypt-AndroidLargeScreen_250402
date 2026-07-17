---
name: project-context-generator
description: >-
  Generates a comprehensive project-context.md documentation file for a
  project. It discovers code directories, build commands, active
  contributors, and architecture. Use when a project lacks onboarding
  documentation or when asked to document a project's context. Don't use
  when instructed to review or update an existing project-context.md.
---

# Project Context Generator

This skill provides a standardized set of instructions to generate a
comprehensive `project-context.md` file. This file helps new developers and AI
agents quickly understand a project's architecture, tech stack, key contacts,
and essential workflows.

## Prerequisites

Before starting, ask the user for the following:

*   The absolute path to the project's root directory.
*   The project documentation root URL (if applicable).
*   Any known issue tracker components/labels or initial design docs.

## 1. Deep-Dive Research & Confirmation

Perform deep-dive research to discover the information needed to fill out the
template.

Copy this checklist and track your progress:

-   [ ] Step 1: Identify Active Contributors
-   [ ] Step 2: Determine Build/Test Commands
-   [ ] Step 3: Find Issue Tracker Components
-   [ ] Step 4: Find Example Pull Requests / Commits
-   [ ] Step 5: Research Architecture

### Step 1: Identify Active Contributors

Use VCS logs (e.g., `git log -n 50`) to find recent contributors. Verify
they are still active project members before adding them to the context file.

### Step 2: Determine Build/Test Commands

Look for configuration or build files (e.g., `package.json`, `Makefile`, `build.gradle`, Unity project settings) and verify commands. Distinguish between commands
for developers building the tool vs end users utilizing it.

### Step 3: Find Issue Tracker Components

Identify the relevant issue tracker category, project, or labels used to manage tasks.

### Step 4: Find Example Pull Requests / Commits

Search recent commits or Pull Requests to find good examples of common code changes or debugging additions.

### Step 5: Research Architecture

Deep dive into the code to understand the architecture. Write a GraphViz
(`.dot`) file and compile it to an SVG diagram.

### Confirmation

**Before generating the final document**, report back your findings to the user
for confirmation. Wait for their approval.

## 2. Document Generation

After the user approves your research findings:

1.  Ensure you are working in a clean workspace on the correct branch.
2.  Synthesize all gathered information into a concise Markdown file following
    the structure of the template provided in
    [project-context.template.md](references/project-context.template.md).
    *   Focus on "Need to Know" information for a developer making their first
        contribution / Pull Request.
    *   If information is missing, leave the section as `[TBD]`.
    *   Use repository-relative paths for all code references.
3.  Write the generated Markdown content to a file named `project-context.md` in
    the root directory of the project.
4.  Embed the SVG architecture diagram in the document.
5.  Commit your changes and create a Pull Request / Code Review.

## Reference Template

The template structure can be found in
[project-context.template.md](references/project-context.template.md).

## Reporting Issues

Report bugs or improvements for this skill via the project's repository issues.