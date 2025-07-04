using ASP.NET.DTO;
using Microsoft.AspNetCore.Mvc;

namespace ASP.NET.Controllers;

[Route("api/[controller]")]
public class ScoreController : Controller
{
    private readonly ScoreService _service;
    private readonly ILogger<ScoreController> _logger;

    public ScoreController(ScoreService service, ILogger<ScoreController> logger)
    {
        _service = service;
        _logger = logger;
    }
    
    //for playFragment add score
    [HttpPost("add")]
    public IActionResult AddScore([FromBody] ScoreRequest scoreRequest)
    {
        try
        {
            _service.AddScore(scoreRequest);
            return Ok();
        }
        catch (Exception exception)
        {
            _logger.LogError("Exception occurred: {Message}", exception.Message);
            return StatusCode(500, "Unexpected error occur.");        }
    }
}