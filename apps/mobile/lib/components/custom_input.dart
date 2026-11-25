import 'package:flutter/widgets.dart';
import '../theme/stitch_theme.dart';

/// Pure `widgets.dart` text input modeled on `eventhub-flutter/lib/components/custom_input.dart`
/// (`EditableText` + managed `FocusNode`) styled with Stitch high-contrast 2px borders.
class CustomInput extends StatefulWidget {
  const CustomInput({
    super.key,
    required this.controller,
    this.label,
    required this.hint,
    this.helperText,
    this.errorText,
    this.prefixIcon,
    this.suffixWidget,
    this.obscureText = false,
    this.keyboardType = TextInputType.text,
    this.onChanged,
  });

  final TextEditingController controller;
  final String? label;
  final String hint;
  final String? helperText;
  final String? errorText;
  final IconData? prefixIcon;
  final Widget? suffixWidget;
  final bool obscureText;
  final TextInputType keyboardType;
  final ValueChanged<String>? onChanged;

  @override
  State<CustomInput> createState() => _CustomInputState();
}

class _CustomInputState extends State<CustomInput> {
  late final FocusNode _focusNode;

  @override
  void initState() {
    super.initState();
    _focusNode = FocusNode()..addListener(_handleFocusChange);
    widget.controller.addListener(_handleTextChange);
  }

  void _handleFocusChange() {
    if (mounted) setState(() {});
  }

  void _handleTextChange() {
    if (mounted) setState(() {});
  }

  @override
  void dispose() {
    _focusNode.removeListener(_handleFocusChange);
    widget.controller.removeListener(_handleTextChange);
    _focusNode.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final borderColor = widget.errorText != null
        ? StitchColors.error
        : _focusNode.hasFocus
            ? StitchColors.primary
            : StitchColors.outline;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        if (widget.label != null && widget.label!.isNotEmpty) ...[
          Text(widget.label!, style: StitchText.labelLg),
          const SizedBox(height: 4),
        ],
        GestureDetector(
          behavior: HitTestBehavior.opaque,
          onTap: () => _focusNode.requestFocus(),
          child: Container(
            constraints: const BoxConstraints(minHeight: 52),
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
            decoration: BoxDecoration(
              color: StitchColors.surfaceContainerLowest,
              borderRadius: BorderRadius.circular(StitchRadius.md),
              border: Border.all(color: borderColor, width: 2),
            ),
            child: Row(
              children: [
                if (widget.prefixIcon != null) ...[
                  Icon(widget.prefixIcon, size: 22, color: StitchColors.outline),
                  const SizedBox(width: 10),
                ],
                Expanded(
                  child: Stack(
                    alignment: Alignment.centerLeft,
                    children: [
                      if (widget.controller.text.isEmpty)
                        IgnorePointer(
                          child: Text(
                            widget.hint,
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis,
                            style: StitchText.bodyLg.copyWith(
                              color: StitchColors.outline,
                            ),
                          ),
                        ),
                      EditableText(
                        controller: widget.controller,
                        focusNode: _focusNode,
                        obscureText: widget.obscureText,
                        keyboardType: widget.keyboardType,
                        onChanged: widget.onChanged,
                        style: StitchText.bodyLg,
                        cursorColor: StitchColors.primary,
                        backgroundCursorColor: StitchColors.outlineVariant,
                      ),
                    ],
                  ),
                ),
                if (widget.suffixWidget != null) ...[
                  const SizedBox(width: 8),
                  widget.suffixWidget!,
                ],
              ],
            ),
          ),
        ),
        if (widget.errorText != null) ...[
          const SizedBox(height: 4),
          Text(
            widget.errorText!,
            style: StitchText.bodyMd.copyWith(color: StitchColors.error),
          ),
        ] else if (widget.helperText != null) ...[
          const SizedBox(height: 4),
          Text(widget.helperText!, style: StitchText.bodyMd),
        ],
      ],
    );
  }
}
