load("//learning/gemini/agents/skill_rules:skills.bzl", "agent_skill")
load("//learning/gemini/agents/skills/skill_creator/scripts:skill_defs.bzl", "validate_skill_test")

package(default_visibility = ["//learning/gemini/agents/skills:__subpackages__"])

validate_skill_test(
    name = "validate_integrate_large_screen_foldable_unity_skill_test",
)

agent_skill(
    name = "integrate_large_screen_foldable_unity",
    srcs = [
        "SKILL.md",
    ],
)
