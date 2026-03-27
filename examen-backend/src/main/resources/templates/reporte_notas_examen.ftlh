<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Acta de Notas</title>
    <style>
        body { font-family: Arial, sans-serif; color: #333; margin: 20px; }
        h1 { color: #0056b3; text-align: center; }
        .header-info { text-align: center; margin-bottom: 30px; font-size: 14px; color: #555; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 10px; text-align: center; }
        th { background-color: #0056b3; color: white; }
    </style>
</head>
<body>

    <h1>Acta Oficial de Calificaciones</h1>
    
    <div class="header-info">
        <p><strong>Examen:</strong> ${examen.titulo!''}</p>
        <p><strong>Descripción:</strong> ${examen.descripcion!'Sin descripción'}</p> <p><strong>Fecha de emisión:</strong> ${fecha}</p>
    </div>

    <table>
        <thead>
            <tr>
                <th>ID Intento</th>
                <th>Alumno (Correo)</th>
                <th>Fecha</th>
                <th>Nota Final</th>
            </tr>
        </thead>
        <tbody>
            <#list listaIntentos as intento>
            <tr>
                <td>${intento.idIntento}</td>
                <td>${intento.usuario.correoUsuario!''}</td>
                <td><#if intento.fechaRealizacion??>${intento.fechaRealizacion.toString()}<#else>Sin fecha</#if></td>
                <td><strong>${intento.nota}/10</strong></td>
            </tr>
            </#list>
        </tbody>
    </table>

</body>
</html>