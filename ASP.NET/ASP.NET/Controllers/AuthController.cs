using Microsoft.AspNetCore.Mvc;

namespace ASP.NET.Controllers;


[Route("api/[controller]")]
public class AuthController : Controller
{
    [HttpPost("login")]
    public IActionResult Login()
    {
        return Ok();
    }
    
}