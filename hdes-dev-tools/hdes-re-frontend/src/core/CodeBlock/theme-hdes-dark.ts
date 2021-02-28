import { AppTheme } from '../Themes'

import { EditorView } from '@codemirror/next/view';
import { HighlightStyle, tags } from '@codemirror/next/highlight';


const themeColors = (theme : AppTheme) => ({
    chalky: "#e5c07b", 
    coral: "#e06c75", 
    cyan: "#cc0054", 
    invalid: "#ffffff", 
    ivory: "#abb2bf", 
    stone: "#5c6370", 
    malibu: "#61afef", 
    sage: "#98c379", 
    whiskey: "#d19a66", 
    violet: "#c678dd", 
    background: theme.palette.background.default, 
    selection: "#405948", 
    cursor: "#528bff"
});

/// The editor theme styles for One Dark.
const hdesDarkTheme = (theme : AppTheme) => {
  const colors = themeColors(theme);
  return EditorView.theme({
    $: {
      color: colors.ivory,
      backgroundColor: colors.background,
      "& ::selection": { backgroundColor: colors.selection },
      caretColor: colors.cursor
    },
    "$$focused": { outline: "unset" },
    "$$focused $cursor": { borderLeftColor: colors.cursor },
    "$$focused $selectionBackground": { backgroundColor: colors.selection },
    $panels: { backgroundColor: colors.background, color: colors.ivory },
    "$panels.top": { borderBottom: "2px solid black" },
    "$panels.bottom": { borderTop: "2px solid black" },
    $searchMatch: {
      backgroundColor: "#72a1ff59",
      outline: "1px solid #457dff"
    },
    "$searchMatch.selected": {
      backgroundColor: "#6199ff2f"
    },
    $activeLine: { backgroundColor: "#2c313c" },
    $selectionMatch: { backgroundColor: "#aafe661a" },
    "$matchingBracket, $nonmatchingBracket": {
      backgroundColor: "#bad0f847",
      outline: "1px solid #515a6b"
    },
    $gutters: {
      backgroundColor: colors.background,
      color: "#545868",
      border: "none"
    },
    "$gutterElement.lineNumber": { color: "inherit" },
    $foldPlaceholder: {
      backgroundColor: "none",
      border: "none",
      color: "#ddd"
    },
    $tooltip: {
      border: "1px solid #181a1f",
      backgroundColor: "#606862"
    },
    "$tooltip.autocomplete": {
      "& > ul > li[aria-selected]": { backgroundColor: colors.background }
    }
  }, { dark: true })
};

/// The highlighting style for code in the One Dark theme.
const hdesDarkHighlightStyle = (theme : AppTheme) => {
  const colors = themeColors(theme);
  return HighlightStyle.define(
    { color: colors.violet,   tag: tags.keyword }, 
    { color: colors.coral,    tag: [tags.name, tags.deleted, tags.character, tags.propertyName, tags.macroName] }, 
    { color: colors.sage,     tag: [tags.processingInstruction, tags.string, tags.inserted] }, 
    { color: colors.malibu,   tag: [tags.function(tags.variableName), tags.labelName] }, 
    { color: colors.whiskey,  tag: [tags.color, tags.constant(tags.name), tags.standard(tags.name)] }, 
    { color: colors.ivory,    tag: [tags.definition(tags.name), tags.separator] }, 
    { color: colors.chalky,   tag: [tags.typeName, tags.className, tags.number, tags.changed, tags.annotation, tags.modifier, tags.self, tags.namespace] }, 
    { color: colors.cyan,     tag: [tags.operator, tags.operatorKeyword, tags.url, tags.escape, tags.regexp, tags.link, tags.special(tags.string)]}, 
    { color: colors.stone,    tag: [tags.meta, tags.comment] },
    { color: colors.whiskey,  tag: [tags.atom, tags.bool, tags.special(tags.variableName)] },
    { color: colors.stone,    tag: tags.link, textDecoration: "underline" }, 
    { color: colors.coral,    tag: tags.heading, fontWeight: "bold" }, 
    { color: colors.invalid,  tag: tags.invalid },
    { fontWeight: "bold",     tag: tags.strong }, 
    { fontStyle: "italic",    tag: tags.emphasis }, 
  )
};

/// Extension to enable the One Dark theme (both the editor theme and
/// the highlight style).
const hdesDark = (theme: AppTheme) => ([hdesDarkTheme(theme), hdesDarkHighlightStyle(theme)]);
export { hdesDark };
