# Desain: Data Desa - Alamat & Kode Pos

## Tujuan
Menambahkan field Alamat (structured) dan Kode Pos ke halaman Data Desa, serta menampilkan kolom Alamat di tabel.

## Database
- `ALTER TABLE desa ADD COLUMN kode_pos VARCHAR(5);`
- `ALTER TABLE desa ADD COLUMN alamat TEXT;`
- Update test data dengan nilai yang masuk akal.

## Tabel (7 kolom)
| Kolom | Lebar | Sumber |
|-------|-------|--------|
| Kecamatan | 120px | Existing |
| Nama Desa | 130px | Existing |
| Kepala Desa | 160px | Existing (synced subquery) |
| **Kode Pos** | 80px | New |
| **Alamat** | 180px | New, truncated with tooltip |
| RT | 60px | Existing |
| RW | 60px | Existing |

## Form Fields
1. Kecamatan (static: Siwalanpanji)
2. Nama Desa (existing)
3. Kepala Desa (read-only, synced)
4. **Kode Pos** (5 digits, input filter)
5. **Alamat** (TextArea)
6. RW/RT section (existing)

## Validasi
- Kode Pos: digits only, max 5 chars

## Sync
- Kepala Desa column tetap auto-sync dari Data Kepala Desa (existing logic)
