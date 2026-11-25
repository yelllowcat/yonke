import 'package:flutter/widgets.dart';
import '../theme/stitch_theme.dart';

/// Primary & Secondary industrial buttons built purely with `widgets.dart`
/// (`GestureDetector` + `AnimatedContainer`), matching the component pattern of
/// `eventhub-flutter` styled with the Stitch Yonke Fácil design system.
class CustomButton extends StatefulWidget {
  const CustomButton({
    super.key,
    required this.text,
    this.onTap,
    this.icon,
    this.isSecondary = false,
    this.subtleBorder = false,
    this.enabled = true,
    this.minHeight = 54,
    this.backgroundColor,
    this.textColor,
  });

  final String text;
  final VoidCallback? onTap;
  final IconData? icon;
  final bool isSecondary;
  final bool subtleBorder;
  final bool enabled;
  final double minHeight;
  final Color? backgroundColor;
  final Color? textColor;

  @override
  State<CustomButton> createState() => _CustomButtonState();
}

class _CustomButtonState extends State<CustomButton> {
  bool _isPressed = false;

  @override
  Widget build(BuildContext context) {
    final active = widget.enabled && widget.onTap != null;
    final Color bg;
    final Color fg;
    final Color border;

    if (!active) {
      bg = StitchColors.surfaceContainerHigh;
      fg = StitchColors.outline;
      border = StitchColors.outlineVariant;
    } else if (widget.isSecondary) {
      bg = _isPressed
          ? StitchColors.secondaryContainer
          : (widget.backgroundColor ?? StitchColors.surfaceContainerLowest);
      fg = widget.textColor ?? StitchColors.secondary;
      border = widget.subtleBorder
          ? (_isPressed ? StitchColors.secondary : StitchColors.outlineVariant)
          : StitchColors.secondary;
    } else {
      final baseBg = widget.backgroundColor ?? StitchColors.primary;
      bg = _isPressed ? StitchColors.onPrimaryContainer : baseBg;
      fg = widget.textColor ?? StitchColors.onPrimary;
      border = bg;
    }

    return Semantics(
      button: true,
      enabled: active,
      label: widget.text,
      child: GestureDetector(
        behavior: HitTestBehavior.opaque,
        onTapDown: active ? (_) => setState(() => _isPressed = true) : null,
        onTapUp: active ? (_) => setState(() => _isPressed = false) : null,
        onTapCancel: active ? () => setState(() => _isPressed = false) : null,
        onTap: active ? widget.onTap : null,
        child: AnimatedContainer(
          duration: const Duration(milliseconds: 90),
          curve: Curves.easeOut,
          constraints: BoxConstraints(minHeight: widget.minHeight),
          width: double.infinity,
          padding: const EdgeInsets.symmetric(horizontal: 18, vertical: 12),
          alignment: Alignment.center,
          transform: _isPressed
              ? Matrix4.diagonal3Values(0.98, 0.98, 1.0)
              : Matrix4.identity(),
          decoration: BoxDecoration(
            color: bg,
            borderRadius: BorderRadius.circular(StitchRadius.md),
            border: Border.all(color: border, width: 2),
          ),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              if (widget.icon != null) ...[
                Icon(widget.icon, size: 22, color: fg),
                const SizedBox(width: 8),
              ],
              Flexible(
                child: Text(
                  widget.text,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  style: StitchText.labelLg.copyWith(color: fg),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class IconSquareButton extends StatefulWidget {
  const IconSquareButton({
    super.key,
    required this.icon,
    required this.semanticLabel,
    required this.onTap,
  });

  final IconData icon;
  final String semanticLabel;
  final VoidCallback onTap;

  @override
  State<IconSquareButton> createState() => _IconSquareButtonState();
}

class _IconSquareButtonState extends State<IconSquareButton> {
  bool _isPressed = false;

  @override
  Widget build(BuildContext context) {
    return Semantics(
      button: true,
      label: widget.semanticLabel,
      child: GestureDetector(
        behavior: HitTestBehavior.opaque,
        onTapDown: (_) => setState(() => _isPressed = true),
        onTapUp: (_) => setState(() => _isPressed = false),
        onTapCancel: () => setState(() => _isPressed = false),
        onTap: widget.onTap,
        child: AnimatedContainer(
          duration: const Duration(milliseconds: 90),
          width: 48,
          height: 48,
          alignment: Alignment.center,
          decoration: BoxDecoration(
            color: _isPressed
                ? StitchColors.surfaceContainer
                : StitchColors.surfaceContainerLowest,
            borderRadius: BorderRadius.circular(StitchRadius.md),
            border: Border.all(color: StitchColors.outline, width: 2),
          ),
          child: Icon(widget.icon, size: 24, color: StitchColors.onSurface),
        ),
      ),
    );
  }
}
