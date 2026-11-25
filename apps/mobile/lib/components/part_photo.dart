import 'dart:io';
import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:flutter/widgets.dart';
import '../theme/stitch_icons.dart';
import '../theme/stitch_theme.dart';

/// Displays a real captured local file (`Image.file`) or uploaded Firebase Storage
/// URL (`Image.network`), with an industrial placeholder fallback.
class PartPhotoWidget extends StatelessWidget {
  const PartPhotoWidget({
    super.key,
    required this.photoPath,
    required this.fallbackReference,
    this.iconSize = 28,
    this.showReferenceLabel = true,
  });

  final String? photoPath;
  final String fallbackReference;
  final double iconSize;
  final bool showReferenceLabel;

  Widget _placeholder() {
    return Container(
      color: StitchColors.surfaceContainer,
      alignment: Alignment.center,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(StitchIcons.build, size: iconSize, color: StitchColors.outline),
          if (showReferenceLabel) ...[
            const SizedBox(height: 4),
            Text(fallbackReference, style: StitchText.monoMd),
          ],
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final path = photoPath;
    if (path == null || path.isEmpty) {
      return _placeholder();
    }

    if (path.startsWith('http://') || path.startsWith('https://')) {
      return Image.network(
        path,
        fit: BoxFit.cover,
        errorBuilder: (_, _, _) => _placeholder(),
      );
    }

    if (!kIsWeb) {
      final file = File(path);
      if (file.existsSync()) {
        return Image.file(
          file,
          fit: BoxFit.cover,
          errorBuilder: (_, _, _) => _placeholder(),
        );
      }
    }

    return _placeholder();
  }
}
