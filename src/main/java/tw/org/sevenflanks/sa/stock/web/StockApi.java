package tw.org.sevenflanks.sa.stock.web;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.org.sevenflanks.sa.base.msg.model.MsgBody;
import tw.org.sevenflanks.sa.stock.service.StockService;

@RestController
@RequestMapping("/api/stock")
public class StockApi {

	@Autowired
	private StockService stockService;

	@GetMapping("/init/{date}")
	public ResponseEntity<MsgBody<String>> init(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws IOException {
		stockService.syncAllToFileAndDb(date);
		return MsgBody.ok("操作成功");
	}

}
