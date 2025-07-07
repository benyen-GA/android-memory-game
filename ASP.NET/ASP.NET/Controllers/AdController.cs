using Microsoft.AspNetCore.Mvc;

namespace ASP.NET.Controllers;


[ApiController ]
[Route("api/[controller]")]
public class AdController : ControllerBase
{
    [HttpGet]
    public IActionResult GetAdUrls()
    {
        var AdFolderPath = Path.Combine(Directory.GetCurrentDirectory(), "wwwroot", "ads");
        if (!Directory.Exists(AdFolderPath))
            return NotFound("Ad Folder not found");
        
        var fileNames = Directory.GetFiles(AdFolderPath).Select(Path.GetFileName).ToList();
        var request = HttpContext.Request;
        var baseUrl = $"{request.Scheme}://{request.Host}";
        var fullUrls = fileNames.Select(name => $"{baseUrl}/ads/{name}");
        return Ok(fullUrls);
        
    }
}