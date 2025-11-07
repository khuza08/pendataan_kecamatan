package pendataankecamatan.controller;

import pendataankecamatan.model.Laporan;

public class ReportController {
    public boolean submitReport(Laporan laporan) {
        // Simulasikan penyimpanan ke database
        System.out.println("Laporan dikirim: " + laporan.getJudul());
        return true;
    }
}
