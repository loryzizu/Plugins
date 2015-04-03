<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!-- ===================== -->
    <!--  WRONG ELEMENT HERE -->
	<xsl:templat match="/">
    <!-- ===================== -->
    
		<div>
			<xsl:for-each select="catalog/cd">
				<p>
					<xsl:value-of select="title" />
				</p>
			</xsl:for-each>
		</div>
	</xsl:template>

</xsl:stylesheet>