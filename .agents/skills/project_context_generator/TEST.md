# Project Context Generator - Agent E2E Test Plan

## Prerequisites

**Read `SKILL.md` first** to understand available commands, flags, and expected
behavior.

This is a doc-only skill that guides the agent through using standard tools
(such as version control, build tools, and issue trackers) to assemble documentation.

--------------------------------------------------------------------------------

## Test 1: Generate full context file

**Prompt:** "Create a `project-context.md` for the hypothetical project in
`<WORKSPACE_PATH>/path/to/project` using standard templates."

**Verify:**

-   Agent starts by asking for setup paths (project path, documentation URL, issue tracker context).
-   Agent identifies Phase 1 (Information Gathering) and waits for response.
-   After response, agent simulates research checklists or planning commands.
-   Final output structure conforms to `project-context.template.md` sections.

--------------------------------------------------------------------------------

## Test 2: Architecture diagram verification

**Prompt:** "I need a project overview for
`<WORKSPACE_PATH>/path/to/project`, make sure to include an architecture
diagram."

**Verify:**

-   Agent identifies that deep-dive research includes creating GraphViz `.dot`
    files.
-   Agent verifies diagram creation triggers compiling to SVG and embedding it
    correctly.

--------------------------------------------------------------------------------

## Test 3: Contributor discovery

**Prompt:** "Generate documentation for project X, paying special attention to
identifying active contributors."

**Verify:**

-   Agent references using `git log -n 50` or standard VCS logs
    for recent activity.
-   Agent verifies it must run checks against project contributors list or workspace directory to
    confirm active state.

--------------------------------------------------------------------------------

## Cleanup

Revert any files created or modified during the test.