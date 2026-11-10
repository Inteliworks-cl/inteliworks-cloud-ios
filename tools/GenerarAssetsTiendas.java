// Genera los graficos de tienda (Google Play / App Store) de Inteliworks Cloud y Talk
// y los iconos de marca para el rebrand iOS, a partir del logo fuente (512x512).
// Uso: java GenerarAssetsTiendas.java <icono.png> <dir-salida-tiendas> <dir-branding-ios>
// Las capturas son MOCKUPS de marca (no capturas reales del dispositivo).
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class GenerarAssetsTiendas {
    static final Color BRAND = new Color(0x1E90D6), DARK = new Color(0x1673AD), DEEP = new Color(0x0E4F7A);
    static final Color BG = new Color(0xF4F7FA), TXT = new Color(0x1F2933), TXT2 = new Color(0x667085), LINE = new Color(0xE4E7EC);
    static final String F = "DejaVu Sans";
    static BufferedImage disc, mark;

    public static void main(String[] a) throws Exception {
        BufferedImage src = ImageIO.read(new File(a[0]));
        File out = new File(a[1]), ios = new File(a[2]);
        disc = crop(src, alphaBox(src, 10));
        mark = crop(toMark(src), alphaBox(toMark(src), 10));

        // --- Iconos ---
        BufferedImage play512 = squareIcon(512, false);
        write(play512, out, "play/cloud/icono-512.png");
        write(play512, out, "play/talk/icono-512.png");
        BufferedImage ios1024 = squareIcon(1024, true);
        write(ios1024, out, "appstore/cloud/icono-1024.png");
        write(ios1024, ios, "Custom.xcassets/AppIcon.appiconset/Icon-App-1024x1024@1x.png");
        write(scaleFit(tint(mark, BRAND), 900), ios, "AppIcon.icon/Assets/iw-glyph.png");
        write(scaleFit(tint(mark, Color.WHITE), 384), ios, "Custom.xcassets/logo.imageset/logo.png");

        // --- Graficos destacados Play 1024x500 ---
        for (String app : new String[]{"cloud", "talk"}) {
            boolean t = app.equals("talk");
            write(feature(t ? "Inteliworks Talk" : "Inteliworks Cloud",
                    t ? "Chat y videollamadas privadas para tu empresa" : "Los archivos de tu empresa, en tu propia nube"),
                    out, "play/" + app + "/grafico-destacado-1024x500.png");
            write(feature(t ? "Inteliworks Talk" : "Inteliworks Cloud",
                    t ? "Private chat and video calls for your business" : "Your company files, in your own cloud"),
                    out, "play/" + app + "/grafico-destacado-1024x500-en.png");
        }

        // --- Capturas (mockups) ---
        String[][] cloudEs = {
                {"Tus archivos,", "en la nube de tu empresa", "files"},
                {"Respaldo automático", "de fotos y videos", "upload"},
                {"Comparte con enlaces", "seguros y con clave", "share"},
                {"Disponible sin conexión", "en terreno y de viaje", "offline"},
                {"Tu propio servidor,", "con soporte en Chile", "login"}};
        String[][] cloudEn = {
                {"Your files,", "in your company cloud", "files"},
                {"Automatic backup", "of photos and videos", "upload"},
                {"Share with secure,", "password-protected links", "share"},
                {"Available offline,", "on site and on the go", "offline"},
                {"Your own server,", "with local support", "login"}};
        String[][] talkEs = {
                {"El chat de", "todo tu equipo", "chats"},
                {"Videollamadas", "privadas y cifradas", "call"},
                {"Comparte archivos", "desde tu nube", "chat"},
                {"Conecta con tu", "Inteliworks Cloud", "tlogin"}};
        String[][] talkEn = {
                {"Chat with", "your whole team", "chats"},
                {"Private, encrypted", "video calls", "call"},
                {"Share files", "from your cloud", "chat"},
                {"Connect to your", "Inteliworks Cloud", "tlogin"}};
        shots(out, "play/cloud/capturas", cloudEs, cloudEn, 1080, 1920, false);
        shots(out, "play/talk/capturas", talkEs, talkEn, 1080, 1920, true);
        shots(out, "appstore/cloud/capturas-iphone-6.9", cloudEs, cloudEn, 1320, 2868, false);
        shots(out, "appstore/cloud/capturas-ipad-13", cloudEs, cloudEn, 2064, 2752, false);
        System.out.println("OK");
    }

    static void shots(File out, String dir, String[][] es, String[][] en, int w, int h, boolean talk) throws Exception {
        for (int i = 0; i < es.length; i++) {
            write(shot(es[i], w, h, true), out, dir + "/es-" + (i + 1) + ".png");
            write(shot(en[i], w, h, false), out, dir + "/en-" + (i + 1) + ".png");
        }
    }

    // ---------- piezas ----------
    static BufferedImage squareIcon(int s, boolean opaqueWhite) {
        BufferedImage o = new BufferedImage(s, s, opaqueWhite ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gfx(o);
        if (opaqueWhite) { // iOS: el sistema redondea; fondo blanco + logo azul (sin alpha, exigido por App Store)
            g.setColor(Color.WHITE); g.fillRect(0, 0, s, s);
            BufferedImage m = scaleFit(tint(mark, BRAND), (int) (s * 0.70));
            g.drawImage(m, (s - m.getWidth()) / 2, (s - m.getHeight()) / 2, null);
        } else {           // Play: cuadrado completo; Play aplica la mascara
            g.setColor(BRAND); g.fillRect(0, 0, s, s);
            BufferedImage d = scaleFit(disc, (int) (s * 0.80));
            g.drawImage(d, (s - d.getWidth()) / 2, (s - d.getHeight()) / 2, null);
        }
        g.dispose();
        return o;
    }

    static BufferedImage feature(String title, String sub) {
        int w = 1024, h = 500;
        BufferedImage o = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = gfx(o);
        g.setPaint(new GradientPaint(0, 0, BRAND, w, h, DEEP)); g.fillRect(0, 0, w, h);
        g.setColor(new Color(255, 255, 255, 18));
        g.fill(new Ellipse2D.Double(620, -180, 620, 620)); g.fill(new Ellipse2D.Double(-160, 300, 420, 420));
        BufferedImage d = scaleFit(disc, 250);
        g.drawImage(d, 80, (h - 250) / 2, null);
        g.setColor(Color.WHITE);
        g.setFont(new Font(F, Font.BOLD, 54)); g.drawString(title, 380, 235);
        g.setFont(new Font(F, Font.PLAIN, 26)); g.setColor(new Color(0xDCEFFB));
        drawWrapped(g, sub, 380, 285, 590, 34);
        g.dispose();
        return o;
    }

    static BufferedImage shot(String[] cap, int w, int h, boolean es) {
        BufferedImage o = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = gfx(o);
        double k = w / 1080.0;
        g.setPaint(new GradientPaint(0, 0, BRAND, 0, h, DEEP)); g.fillRect(0, 0, w, h);
        g.setColor(Color.WHITE);
        int fs = (int) (74 * k);
        g.setFont(new Font(F, Font.BOLD, fs));
        center(g, cap[0], w / 2, (int) (190 * k));
        g.setFont(new Font(F, Font.PLAIN, (int) (56 * k)));
        g.setColor(new Color(0xDCEFFB));
        center(g, cap[1], w / 2, (int) (275 * k));
        // "telefono"
        int pw = (int) (Math.min(w * 0.80, h * 0.46)), px = (w - pw) / 2, py = (int) (360 * k);
        int ph = h - py + (int) (60 * k);
        RoundRectangle2D body = new RoundRectangle2D.Double(px, py, pw, ph, 70 * k, 70 * k);
        g.setColor(new Color(0, 0, 0, 60)); g.fill(new RoundRectangle2D.Double(px + 8 * k, py + 14 * k, pw, ph, 70 * k, 70 * k));
        g.setColor(new Color(0x101828)); g.fill(body);
        int bz = (int) (18 * k);
        Shape screen = new RoundRectangle2D.Double(px + bz, py + bz, pw - 2 * bz, ph - 2 * bz, 54 * k, 54 * k);
        Graphics2D s = (Graphics2D) g.create();
        s.clip(screen);
        s.translate(px + bz, py + bz);
        screenUI(s, cap[2], pw - 2 * bz, ph - 2 * bz, es, pw / 860.0);
        s.dispose();
        g.dispose();
        return o;
    }

    // ---------- pantallas ----------
    static void screenUI(Graphics2D g, String kind, int w, int h, boolean es, double k) {
        g.setColor(BG); g.fillRect(0, 0, w, h);
        // barra de estado
        g.setColor(TXT); g.setFont(new Font(F, Font.BOLD, (int) (26 * k)));
        g.drawString("9:41", (int) (40 * k), (int) (52 * k));
        g.fillRoundRect(w - (int) (95 * k), (int) (30 * k), (int) (50 * k), (int) (24 * k), 8, 8);
        boolean talk = kind.equals("chats") || kind.equals("call") || kind.equals("chat") || kind.equals("tlogin");
        switch (kind) {
            case "login": case "tlogin": login(g, w, h, es, k, talk); return;
            case "call": call(g, w, h, es, k); return;
        }
        String title = switch (kind) {
            case "files" -> es ? "Todos los archivos" : "All files";
            case "upload" -> es ? "Subida automática" : "Auto upload";
            case "share" -> "Contratos 2026.pdf";
            case "offline" -> es ? "Disponible sin conexión" : "Available offline";
            case "chats" -> "Inteliworks Talk";
            default -> es ? "Equipo Comercial" : "Sales team";
        };
        appBar(g, w, k, title);
        int y = (int) (200 * k);
        switch (kind) {
            case "files" -> {
                String[][] it = es ? new String[][]{{"D", "Documentos", "hace 2 min"}, {"D", "Fotos", "ayer"}, {"D", "Contabilidad 2026", "3 nov"},
                        {"P", "Contrato servicio.pdf", "1,2 MB · hoy"}, {"X", "Ventas octubre.xlsx", "86 KB · ayer"}, {"I", "Obra terreno.jpg", "3,4 MB · 2 nov"},
                        {"W", "Propuesta cliente.docx", "220 KB · 1 nov"}}
                        : new String[][]{{"D", "Documents", "2 min ago"}, {"D", "Photos", "yesterday"}, {"D", "Accounting 2026", "Nov 3"},
                        {"P", "Service contract.pdf", "1.2 MB · today"}, {"X", "October sales.xlsx", "86 KB · yesterday"}, {"I", "Site photo.jpg", "3.4 MB · Nov 2"},
                        {"W", "Client proposal.docx", "220 KB · Nov 1"}};
                for (String[] r : it) { y = row(g, w, y, k, r[0], r[1], r[2], false); if (y > h) break; }
                fab(g, w, h, k);
            }
            case "upload" -> {
                y = toggle(g, w, y, k, es ? "Cámara" : "Camera", es ? "/Fotos/Cámara · solo con Wi-Fi" : "/Photos/Camera · Wi-Fi only", true);
                y = toggle(g, w, y, k, "WhatsApp Images", es ? "/Fotos/WhatsApp" : "/Photos/WhatsApp", true);
                y = toggle(g, w, y, k, es ? "Capturas de pantalla" : "Screenshots", es ? "/Fotos/Capturas" : "/Photos/Screenshots", false);
                y = toggle(g, w, y, k, "Videos", es ? "/Videos · solo cargando" : "/Videos · while charging", true);
                y += (int) (30 * k);
                card(g, (int) (30 * k), y, w - (int) (60 * k), (int) (150 * k), k);
                g.setColor(TXT); g.setFont(new Font(F, Font.BOLD, (int) (30 * k)));
                g.drawString(es ? "Subiendo 12 de 48 fotos" : "Uploading 12 of 48 photos", (int) (60 * k), y + (int) (60 * k));
                g.setColor(LINE); g.fillRoundRect((int) (60 * k), y + (int) (90 * k), w - (int) (120 * k), (int) (16 * k), 16, 16);
                g.setColor(BRAND); g.fillRoundRect((int) (60 * k), y + (int) (90 * k), (int) ((w - 120 * k) * 0.25), (int) (16 * k), 16, 16);
            }
            case "share" -> {
                y = row(g, w, y, k, "P", "Contratos 2026.pdf", "1,2 MB", false);
                y += (int) (20 * k);
                card(g, (int) (30 * k), y, w - (int) (60 * k), (int) (420 * k), k);
                int yy = y + (int) (65 * k);
                g.setColor(TXT); g.setFont(new Font(F, Font.BOLD, (int) (32 * k)));
                g.drawString(es ? "Enlace para compartir" : "Share link", (int) (60 * k), yy);
                yy += (int) (70 * k);
                g.setColor(new Color(0xEAF5FC)); g.fillRoundRect((int) (60 * k), yy - (int) (45 * k), w - (int) (120 * k), (int) (70 * k), 20, 20);
                g.setColor(DARK); g.setFont(new Font(F, Font.PLAIN, (int) (26 * k)));
                g.drawString("cloud.tuempresa.cl/s/Xk29…", (int) (85 * k), yy);
                yy += (int) (90 * k);
                yy = check(g, yy, k, es ? "Proteger con contraseña" : "Password protect", true);
                yy = check(g, yy, k, es ? "Vence el 30-11-2026" : "Expires Nov 30, 2026", true);
                check(g, yy, k, es ? "Permitir edición" : "Allow editing", false);
                y += (int) (460 * k);
                button(g, (int) (30 * k), y, w - (int) (60 * k), k, es ? "Copiar enlace" : "Copy link");
            }
            case "offline" -> {
                String[][] it = es ? new String[][]{{"P", "Planos obra.pdf", "Sincronizado"}, {"X", "Inventario bodega.xlsx", "Sincronizado"},
                        {"W", "Procedimientos.docx", "Sincronizado"}, {"I", "Fachada.jpg", "Sincronizado"}, {"D", "Manuales", "8 archivos"}}
                        : new String[][]{{"P", "Site plans.pdf", "Synced"}, {"X", "Warehouse stock.xlsx", "Synced"},
                        {"W", "Procedures.docx", "Synced"}, {"I", "Front.jpg", "Synced"}, {"D", "Manuals", "8 files"}};
                for (String[] r : it) y = row(g, w, y, k, r[0], r[1], r[2], true);
            }
            case "chats" -> {
                String[][] it = es ? new String[][]{{"Equipo Comercial", "Ana: Envié la cotización ✔", "10:24"}, {"Soporte", "Ticket #128 resuelto", "09:58"},
                        {"Gerencia", "Reunión a las 15:00", "ayer"}, {"Bodega", "Llegó el pedido", "ayer"}, {"Proyecto Norte", "Pedro: subí los planos", "lun"}}
                        : new String[][]{{"Sales team", "Ana: Quote sent ✔", "10:24"}, {"Support", "Ticket #128 solved", "09:58"},
                        {"Management", "Meeting at 3 pm", "yesterday"}, {"Warehouse", "Order arrived", "yesterday"}, {"North project", "Pedro: plans uploaded", "Mon"}};
                for (String[] r : it) y = chatRow(g, w, y, k, r[0], r[1], r[2]);
            }
            default -> { // chat
                y = bubble(g, w, y, k, es ? "Hola equipo, ¿quién tiene la última versión del contrato?" : "Hi team, who has the latest contract?", false);
                y = bubble(g, w, y, k, es ? "Yo, te la comparto desde la nube" : "Me, sharing it from the cloud", true);
                card(g, w / 2 - (int) (40 * k), y, w / 2, (int) (120 * k), k);
                fileIcon(g, w / 2 - (int) (10 * k), y + (int) (25 * k), k, "P");
                g.setColor(TXT); g.setFont(new Font(F, Font.BOLD, (int) (24 * k)));
                g.drawString(es ? "Contrato.pdf" : "Contract.pdf", w / 2 + (int) (75 * k), y + (int) (60 * k));
                g.setColor(TXT2); g.setFont(new Font(F, Font.PLAIN, (int) (22 * k)));
                g.drawString("1,2 MB", w / 2 + (int) (75 * k), y + (int) (92 * k));
                y += (int) (150 * k);
                bubble(g, w, y, k, es ? "¡Gracias!" : "Thanks!", false);
            }
        }
    }

    static void appBar(Graphics2D g, int w, double k, String title) {
        g.setColor(Color.WHITE); g.fillRect(0, (int) (80 * k), w, (int) (100 * k));
        g.setColor(LINE); g.fillRect(0, (int) (180 * k), w, 2);
        g.setColor(TXT); g.setFont(new Font(F, Font.BOLD, (int) (34 * k)));
        g.drawString(title, (int) (40 * k), (int) (145 * k));
        BufferedImage m = scaleFit(disc, (int) (60 * k));
        g.drawImage(m, w - (int) (100 * k), (int) (100 * k), null);
    }

    static int row(Graphics2D g, int w, int y, double k, String type, String name, String meta, boolean offline) {
        int rh = (int) (120 * k);
        g.setColor(Color.WHITE); g.fillRect(0, y, w, rh);
        g.setColor(LINE); g.fillRect((int) (140 * k), y + rh - 1, w, 1);
        fileIcon(g, (int) (40 * k), y + (int) (22 * k), k, type);
        g.setColor(TXT); g.setFont(new Font(F, Font.PLAIN, (int) (30 * k)));
        g.drawString(name, (int) (150 * k), y + (int) (55 * k));
        g.setColor(TXT2); g.setFont(new Font(F, Font.PLAIN, (int) (23 * k)));
        g.drawString(meta, (int) (150 * k), y + (int) (92 * k));
        if (offline) {
            g.setColor(new Color(0x12B76A));
            g.fill(new Ellipse2D.Double(w - 80 * k, y + 40 * k, 40 * k, 40 * k));
            g.setColor(Color.WHITE); g.setStroke(new BasicStroke((float) (5 * k)));
            g.draw(new Line2D.Double(w - 70 * k, y + 60 * k, w - 62 * k, y + 70 * k));
            g.draw(new Line2D.Double(w - 62 * k, y + 70 * k, w - 48 * k, y + 52 * k));
        } else {
            g.setColor(TXT2);
            for (int i = 0; i < 3; i++) g.fill(new Ellipse2D.Double(w - 60 * k, y + (40 + i * 16) * k, 8 * k, 8 * k));
        }
        return y + rh;
    }

    static void fileIcon(Graphics2D g, int x, int y, double k, String t) {
        int s = (int) (76 * k);
        Color c = switch (t) { case "D" -> BRAND; case "P" -> new Color(0xE5484D); case "X" -> new Color(0x16A34A);
            case "W" -> new Color(0x2563EB); default -> new Color(0xF59E0B); };
        g.setColor(c);
        if (t.equals("D")) {
            g.fillRoundRect(x, y + s / 6, s, (int) (s * 0.78), 12, 12);
            g.fillRoundRect(x, y + s / 10, s / 2, s / 4, 10, 10);
        } else {
            g.fillRoundRect(x + s / 8, y, (int) (s * 0.75), s, 12, 12);
            g.setColor(Color.WHITE); g.setFont(new Font(F, Font.BOLD, (int) (22 * k)));
            String l = switch (t) { case "P" -> "PDF"; case "X" -> "XLS"; case "W" -> "DOC"; default -> "JPG"; };
            FontMetrics fm = g.getFontMetrics();
            g.drawString(l, x + s / 2 - fm.stringWidth(l) / 2, y + s / 2 + fm.getAscent() / 3);
        }
    }

    static int toggle(Graphics2D g, int w, int y, double k, String a, String b, boolean on) {
        int rh = (int) (130 * k);
        g.setColor(Color.WHITE); g.fillRect(0, y, w, rh);
        g.setColor(LINE); g.fillRect((int) (40 * k), y + rh - 1, w, 1);
        g.setColor(TXT); g.setFont(new Font(F, Font.PLAIN, (int) (30 * k)));
        g.drawString(a, (int) (40 * k), y + (int) (58 * k));
        g.setColor(TXT2); g.setFont(new Font(F, Font.PLAIN, (int) (23 * k)));
        g.drawString(b, (int) (40 * k), y + (int) (95 * k));
        int tw = (int) (90 * k), th = (int) (50 * k), tx = w - tw - (int) (40 * k), ty = y + (rh - th) / 2;
        g.setColor(on ? BRAND : new Color(0xD0D5DD)); g.fillRoundRect(tx, ty, tw, th, th, th);
        g.setColor(Color.WHITE); g.fillOval(on ? tx + tw - th + 5 : tx + 5, ty + 5, th - 10, th - 10);
        return y + rh;
    }

    static int check(Graphics2D g, int y, double k, String t, boolean on) {
        int s = (int) (40 * k), x = (int) (60 * k);
        g.setColor(on ? BRAND : new Color(0xD0D5DD)); g.fillRoundRect(x, y - s + (int) (8 * k), s, s, 10, 10);
        if (on) {
            g.setColor(Color.WHITE); g.setStroke(new BasicStroke((float) (5 * k)));
            g.draw(new Line2D.Double(x + 9 * k, y - 12 * k, x + 17 * k, y - 4 * k));
            g.draw(new Line2D.Double(x + 17 * k, y - 4 * k, x + 31 * k, y - 22 * k));
        }
        g.setColor(TXT); g.setFont(new Font(F, Font.PLAIN, (int) (28 * k)));
        g.drawString(t, x + s + (int) (25 * k), y);
        return y + (int) (80 * k);
    }

    static int chatRow(Graphics2D g, int w, int y, double k, String name, String msg, String time) {
        int rh = (int) (140 * k);
        g.setColor(Color.WHITE); g.fillRect(0, y, w, rh);
        g.setColor(LINE); g.fillRect((int) (150 * k), y + rh - 1, w, 1);
        g.setColor(new Color(Color.HSBtoRGB((name.hashCode() & 0xff) / 255f, 0.45f, 0.80f)));
        g.fillOval((int) (35 * k), y + (int) (25 * k), (int) (90 * k), (int) (90 * k));
        g.setColor(Color.WHITE); g.setFont(new Font(F, Font.BOLD, (int) (36 * k)));
        center(g, name.substring(0, 1), (int) (80 * k), y + (int) (83 * k));
        g.setColor(TXT); g.setFont(new Font(F, Font.BOLD, (int) (29 * k)));
        g.drawString(name, (int) (150 * k), y + (int) (60 * k));
        g.setColor(TXT2); g.setFont(new Font(F, Font.PLAIN, (int) (24 * k)));
        g.drawString(msg, (int) (150 * k), y + (int) (100 * k));
        g.drawString(time, w - (int) (130 * k), y + (int) (60 * k));
        return y + rh;
    }

    static int bubble(Graphics2D g, int w, int y, double k, String t, boolean mine) {
        g.setFont(new Font(F, Font.PLAIN, (int) (27 * k)));
        int maxw = (int) (w * 0.68), lh = (int) (38 * k);
        java.util.List<String> lines = wrap(g, t, maxw - (int) (50 * k));
        int bw = 0; for (String l : lines) bw = Math.max(bw, g.getFontMetrics().stringWidth(l));
        bw += (int) (50 * k);
        int bh = lines.size() * lh + (int) (36 * k);
        int x = mine ? w - bw - (int) (30 * k) : (int) (30 * k);
        g.setColor(mine ? BRAND : Color.WHITE); g.fillRoundRect(x, y, bw, bh, (int) (36 * k), (int) (36 * k));
        g.setColor(mine ? Color.WHITE : TXT);
        int yy = y + (int) (18 * k) + lh - (int) (8 * k);
        for (String l : lines) { g.drawString(l, x + (int) (25 * k), yy); yy += lh; }
        return y + bh + (int) (30 * k);
    }

    static void call(Graphics2D g, int w, int h, boolean es, double k) {
        g.setColor(new Color(0x101828)); g.fillRect(0, 0, w, h);
        String[] n = {"Ana", "Pedro", "Carla", "Jorge"};
        int cw = w / 2, ch = (int) ((h - 380 * k) / 2), top = (int) (90 * k);
        for (int i = 0; i < 4; i++) {
            int x = (i % 2) * cw, y = top + (i / 2) * ch;
            g.setColor(new Color(Color.HSBtoRGB(i * 0.21f + 0.5f, 0.35f, 0.35f)));
            g.fillRect(x + 6, y + 6, cw - 12, ch - 12);
            g.setColor(new Color(Color.HSBtoRGB(i * 0.21f + 0.5f, 0.45f, 0.85f)));
            int d = (int) (170 * k);
            g.fillOval(x + cw / 2 - d / 2, y + ch / 2 - d / 2, d, d);
            g.setColor(Color.WHITE); g.setFont(new Font(F, Font.BOLD, (int) (70 * k)));
            center(g, n[i].substring(0, 1), x + cw / 2, y + ch / 2 + (int) (25 * k));
            g.setFont(new Font(F, Font.PLAIN, (int) (26 * k)));
            g.drawString(n[i], x + (int) (25 * k), y + ch - (int) (25 * k));
        }
        int by = h - (int) (200 * k);
        Color[] c = {new Color(0x344054), new Color(0x344054), new Color(0x344054), new Color(0xE5484D)};
        for (int i = 0; i < 4; i++) {
            int d = (int) (110 * k), x = w / 2 - 2 * d - (int) (45 * k) + i * (d + (int) (30 * k));
            g.setColor(c[i]); g.fillOval(x, by, d, d);
        }
        g.setColor(Color.WHITE); g.setFont(new Font(F, Font.BOLD, (int) (28 * k)));
        center(g, es ? "Reunión semanal · 12:34" : "Weekly meeting · 12:34", w / 2, (int) (70 * k));
    }

    static void login(Graphics2D g, int w, int h, boolean es, double k, boolean talk) {
        g.setPaint(new GradientPaint(0, 0, BRAND, 0, h, DARK)); g.fillRect(0, 0, w, h);
        BufferedImage d = scaleFit(disc, (int) (260 * k));
        g.drawImage(d, (w - d.getWidth()) / 2, (int) (220 * k), null);
        g.setColor(Color.WHITE); g.setFont(new Font(F, Font.BOLD, (int) (46 * k)));
        center(g, talk ? "Inteliworks Talk" : "Inteliworks Cloud", w / 2, (int) (580 * k));
        g.setFont(new Font(F, Font.PLAIN, (int) (26 * k))); g.setColor(new Color(0xDCEFFB));
        center(g, es ? "Dirección de tu servidor" : "Your server address", w / 2, (int) (700 * k));
        int fx = (int) (60 * k), fw = w - 2 * fx, fy = (int) (730 * k);
        g.setColor(Color.WHITE); g.fillRoundRect(fx, fy, fw, (int) (90 * k), 24, 24);
        g.setColor(TXT); g.setFont(new Font(F, Font.PLAIN, (int) (28 * k)));
        g.drawString("https://cloud.tuempresa.cl", fx + (int) (30 * k), fy + (int) (57 * k));
        int bY = fy + (int) (140 * k);
        g.setColor(DEEP); g.fillRoundRect(fx, bY, fw, (int) (90 * k), 24, 24);
        g.setColor(Color.WHITE); g.setFont(new Font(F, Font.BOLD, (int) (30 * k)));
        center(g, es ? "Iniciar sesión" : "Log in", w / 2, bY + (int) (57 * k));
        g.setFont(new Font(F, Font.PLAIN, (int) (22 * k))); g.setColor(new Color(0xDCEFFB));
        center(g, "soporte@inteliworks.cl", w / 2, h - (int) (80 * k));
    }

    static void card(Graphics2D g, int x, int y, int w, int h, double k) {
        g.setColor(new Color(0, 0, 0, 15)); g.fillRoundRect(x, y + 4, w, h, (int) (30 * k), (int) (30 * k));
        g.setColor(Color.WHITE); g.fillRoundRect(x, y, w, h, (int) (30 * k), (int) (30 * k));
    }

    static void button(Graphics2D g, int x, int y, int w, double k, String t) {
        g.setColor(BRAND); g.fillRoundRect(x, y, w, (int) (95 * k), (int) (30 * k), (int) (30 * k));
        g.setColor(Color.WHITE); g.setFont(new Font(F, Font.BOLD, (int) (30 * k)));
        center(g, t, x + w / 2, y + (int) (60 * k));
    }

    static void fab(Graphics2D g, int w, int h, double k) {
        int d = (int) (130 * k), x = w - d - (int) (50 * k), y = h - d - (int) (140 * k);
        if (y < 0) return;
        g.setColor(BRAND); g.fillRoundRect(x, y, d, d, (int) (40 * k), (int) (40 * k));
        g.setColor(Color.WHITE); g.setStroke(new BasicStroke((float) (9 * k)));
        g.drawLine(x + d / 2, y + d / 3, x + d / 2, y + 2 * d / 3); g.drawLine(x + d / 3, y + d / 2, x + 2 * d / 3, y + d / 2);
    }

    // ---------- util ----------
    static Graphics2D gfx(BufferedImage o) {
        Graphics2D g = o.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        return g;
    }

    static void center(Graphics2D g, String s, int cx, int y) { g.drawString(s, cx - g.getFontMetrics().stringWidth(s) / 2, y); }

    static java.util.List<String> wrap(Graphics2D g, String t, int maxw) {
        java.util.List<String> out = new java.util.ArrayList<>();
        String cur = "";
        for (String wd : t.split(" ")) {
            String n = cur.isEmpty() ? wd : cur + " " + wd;
            if (g.getFontMetrics().stringWidth(n) > maxw && !cur.isEmpty()) { out.add(cur); cur = wd; } else cur = n;
        }
        if (!cur.isEmpty()) out.add(cur);
        return out;
    }

    static void drawWrapped(Graphics2D g, String t, int x, int y, int maxw, int lh) {
        for (String l : wrap(g, t, maxw)) { g.drawString(l, x, y); y += lh; }
    }

    static Rectangle alphaBox(BufferedImage im, int th) {
        int x0 = im.getWidth(), y0 = im.getHeight(), x1 = -1, y1 = -1;
        for (int y = 0; y < im.getHeight(); y++)
            for (int x = 0; x < im.getWidth(); x++)
                if ((im.getRGB(x, y) >>> 24) > th) { x0 = Math.min(x0, x); y0 = Math.min(y0, y); x1 = Math.max(x1, x); y1 = Math.max(y1, y); }
        return new Rectangle(x0, y0, x1 - x0 + 1, y1 - y0 + 1);
    }

    static BufferedImage crop(BufferedImage im, Rectangle r) {
        int s = Math.max(r.width, r.height);
        BufferedImage o = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = o.createGraphics();
        g.drawImage(im, (s - r.width) / 2 - r.x, (s - r.height) / 2 - r.y, null);
        g.dispose();
        return o;
    }

    static BufferedImage toMark(BufferedImage im) { // blanco -> transparente; azul -> opaco
        BufferedImage o = new BufferedImage(im.getWidth(), im.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < im.getHeight(); y++)
            for (int x = 0; x < im.getWidth(); x++) {
                int p = im.getRGB(x, y), a = p >>> 24, r = (p >> 16) & 255, gg = (p >> 8) & 255, b = p & 255;
                int al = Math.min(255, (255 - Math.min(r, Math.min(gg, b))) * 255 / 225) * a / 255;
                o.setRGB(x, y, (al << 24) | 0x1E90D6);
            }
        return o;
    }

    static BufferedImage tint(BufferedImage im, Color c) {
        BufferedImage o = new BufferedImage(im.getWidth(), im.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < im.getHeight(); y++)
            for (int x = 0; x < im.getWidth(); x++) o.setRGB(x, y, (im.getRGB(x, y) & 0xff000000) | (c.getRGB() & 0xffffff));
        return o;
    }

    static BufferedImage scaleFit(BufferedImage im, int s) { // escalado en pasos (calidad)
        BufferedImage cur = im;
        while (cur.getWidth() / 2 >= s) cur = resize(cur, cur.getWidth() / 2);
        return resize(cur, s);
    }

    static BufferedImage resize(BufferedImage im, int s) {
        BufferedImage o = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = gfx(o); g.drawImage(im, 0, 0, s, s, null); g.dispose();
        return o;
    }

    static void write(BufferedImage im, File base, String rel) throws Exception {
        File f = new File(base, rel); f.getParentFile().mkdirs();
        ImageIO.write(im, "png", f);
    }
}
