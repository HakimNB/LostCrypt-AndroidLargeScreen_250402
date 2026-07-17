---
name: syncing-docs-and-code
description: >-
  Audits and synchronizes documentation with source code following an iterative design-to-code lifecycle. Generates or updates Markdown design documents from implementation changes, guides code generation from approved design docs, and verifies internal documentation freshness. Use when creating or updating .md design documents within the project directory, syncing Markdown documentation after code changes or Pull Request reviews, auditing implementation drift against design specs, or running documentation freshness utilities. Don't use for general code refactoring without documentation impact, writing standalone unit tests, or creating non-technical user guides.
---

# Syncing Docs and Code

Automate the bidirectional synchronization of Markdown documentation and source code using an iterative development lifecycle.

## Why Syncing Matters

In complex systems, implementation drift happens when code evolves without updating the underlying design documentation. Maintaining parity between `.md` design specifications and source code ensures that developer onboarding, AI coding assistants, and automated tools operate on ground-truth architecture rather than stale assumptions.

---

## 1. Workflow: Updating Docs from Code Changes (Post-Implementation Sync)

When source code deviates from or expands upon an approved design doc, update the documentation to reflect the live implementation.

1. **Read authoritative context**: Read the existing `.md` design document within the project directory and the modified implementation files.
2. **Perform gap analysis**: Identify discrepancies between documented architecture, data models, or API endpoints and the actual code.
3. **Update Markdown in place**: Edit the design doc to align with the implementation while preserving historical context:
   - Adjust architectural diagrams or descriptions to match new structures.
   - Update code blocks, proto schemas, and JSON payloads to match live contracts.
   - Add documentation for new feature flags or environment configurations.
4. **Provide review link**: Provide the developer with a clickable link to the updated document for verification.

---

## 2. Workflow: Design-Guided Code Implementation

When implementing a new feature or refactoring from an approved design doc, treat the document as the primary specification.

1. **Load design doc**: Read the target `.md` design document within the project directory before writing code.
2. **Resolve ambiguities early**: If the document contains multiple paths forward or underspecified requirements, propose exactly 3 distinct implementation options to the user before generating code.
3. **Generate aligned code**: Write or modify source files to adhere strictly to the documented goals, architecture, and data structures.
4. **Audit compliance**: Verify that all documented constraints (such as OAuth scope restrictions or token budgeting rules) are respected in the implementation.

---

## 3. Workflow: Interactive Writing & Refinement Dialog

When drafting a new design doc or refining an existing one, follow this exact sequence of verification steps. Complete only one step at a time and update the document after each step.

Step | Action | Prompt User?
---- | ------ | ------------
1. Clarify Ambiguities | Identify and ask questions to resolve immediate architectural or requirement ambiguities. | Yes
2. Examine Assumptions | Check for underlying technical or operational assumptions and surface them for validation. | Yes
3. Readability Audit | Ensure terminology and structure are clear for newcomers to the domain. Refine headings and flow. | No
4. Security & Privacy Audit | Audit against general data security and privacy principles. Ensure mitigations are documented. | Yes
5. Publish & Link | Write or update the `.md` file within the project directory using `UPPER_SNAKE_CASE.md` and provide a clickable review link. | Yes

---

## 4. Documentation Freshness

When documentation changes are finalized, ensure freshness monitors are synchronized.

### Freshness Markers

When reviewing or generating reference documentation, verify if the project uses any freshness markers or automated documentation checkers to ensure the content remains updated.
