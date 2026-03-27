<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Reporte de Usuarios</title>
    <style>
        body { font-family: Arial, sans-serif; color: #333; margin: 20px; }
        h1 { color: #0056b3; text-align: center; }
        .fecha { text-align: right; font-style: italic; font-size: 12px; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f4f4f4; color: #333; font-weight: bold; }
        .footer { margin-top: 30px; text-align: center; font-size: 10px; color: #777; }
    </style>
</head>
<body>

    <h1>Reporte de Usuarios Activos</h1>
    <div class="fecha">
        Fecha de generación: <span>${fecha}</span>
    </div>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Apellidos</th>
                <th>Correo Electrónico</th>
            </tr>
        </thead>
        <tbody>
            <#list listaUsuarios as usuario>
            <tr>
                <td>${usuario.idUsuario}</td>
                <td>${usuario.nombreUsuario!''}</td>
                <td>${usuario.apellidoUsuario!''}</td>
                <td>${usuario.correoUsuario}</td>
            </tr>
            </#list>
        </tbody>
    </table>

    <div class="footer">
        Generador de Exámenes
    </div>

</body>
</html>